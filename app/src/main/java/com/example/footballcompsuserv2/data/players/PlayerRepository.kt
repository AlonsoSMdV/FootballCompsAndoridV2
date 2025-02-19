package com.example.footballcompsuserv2.data.players

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.core.net.toUri
import com.example.footballcompsuserv2.data.leagues.localToExternal
import com.example.footballcompsuserv2.data.local.ILocalDataSource
import com.example.footballcompsuserv2.data.remote.players.IPlayerRemoteDataSource
import com.example.footballcompsuserv2.data.remote.players.PlayerCreate
import com.example.footballcompsuserv2.data.remote.players.PlayerRaw
import com.example.footballcompsuserv2.data.remote.players.PlayerResponse
import com.example.footballcompsuserv2.data.remote.players.PlayerUpdate
import com.example.footballcompsuserv2.data.remote.uploadImg.StrapiResponse
import com.example.footballcompsuserv2.data.teams.localToExternal
import com.example.footballcompsuserv2.di.NetworkModule
import com.example.footballcompsuserv2.di.NetworkUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import javax.inject.Inject

class PlayerRepository @Inject constructor(
    private val remoteData: IPlayerRemoteDataSource,
    private val local: ILocalDataSource, // Fuente de datos local
    private val networkUtils: NetworkUtils, // Verifica la conexión de red
    @ApplicationContext private val context: Context
) : IPlayerRepository {

    private val _state = MutableStateFlow<List<Player>>(listOf())
    override val setStream: StateFlow<List<Player>>
        get() = _state.asStateFlow()

    override suspend fun readAll(): List<Player> {
        return if (networkUtils.isNetworkAvailable()) {
            val res = remoteData.readAll()
            if (res.isSuccessful) {
                val playerList = res.body()?.data ?: emptyList()
                _state.value = playerList.toExternal()

                // Guardar en base de datos local
                playerList.forEach { player ->
                    local.createLocalPlayer(player.toExternal().toLocal())
                }
                playerList.toExternal()
            } else {
                _state.value
            }
        } else {
            Toast.makeText(context, "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
            _state.value
        }
    }

    override suspend fun readFavs(isFav: Boolean): List<Player> {
        return if (networkUtils.isNetworkAvailable()) {
            val filters = mapOf(
                "filters[isFavourite][\$eq]" to isFav
            )
            val res = remoteData.readFavs(filters)
            if (res.isSuccessful) {
                val playerList = res.body()?.data ?: emptyList()
                _state.value = playerList.toExternal()
                return _state.value.filter { it.isFavourite }
            }
            emptyList()
        } else {
            local.getFavLocalPlayers().collect { localPlayers ->
                _state.value = localPlayers.localToExternal().filter { it.isFavourite }
            }
            _state.value.filter { it.isFavourite }
        }
    }

    override suspend fun readPlayersByTeam(teamId: Int): List<Player> {
        return if (networkUtils.isNetworkAvailable()) {
            val filters = mapOf(
                "filters[team][id][\$eq]" to teamId.toString()
            )
            val res = remoteData.readPlayersByTeam(filters)
            if (res.isSuccessful) {
                val playerList = res.body()?.data ?: emptyList()
                _state.value = playerList.toExternal()
                return _state.value
            }
            _state.value
        } else {
            local.getLocalPlayersByTeam(teamId).collect { localPlayers ->
                _state.value = localPlayers.localToExternal()
            }
            _state.value
        }
    }

    override suspend fun readOne(id: Int): Player {
        return if (networkUtils.isNetworkAvailable()) {
            val res = remoteData.readOne(id)
            res.body()?.data?.toExternal()
                ?: Player("0", "", "", "", "", "", 0, "", false, null, "")
        } else {
            val localPlayer = local.getLocalOnePlayer(id)
            localPlayer?.localToExternal() ?: Player("0", "", "", "", "", "", 0, "", false, null, "")
        }
    }

    override suspend fun createPlayer(player: PlayerCreate, photo: Uri?): Response<StrapiResponse<PlayerRaw>> {
        return if (networkUtils.isNetworkAvailable()) {
            val response = remoteData.createPlayer(player)
            if (response.isSuccessful) {
                val uploadedPlayer = response.body()
                photo?.let { uri -> uploadPlayerPhoto(uri, uploadedPlayer!!.data.id) }
            }
            response
        } else {
            Toast.makeText(context, "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
            Response.error(400, "No hay conexión".toResponseBody(null)) // Simulación de error
        }
    }

    override suspend fun deletePlayer(id: Int) {
        if (networkUtils.isNetworkAvailable()) {
            remoteData.deletePlayer(id)
        } else {
            Toast.makeText(context, "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
        }
    }

    override suspend fun updatePlayer(id: Int, player: PlayerUpdate) {
        if (networkUtils.isNetworkAvailable()) {
            remoteData.updatePlayer(id, player)
        } else {
            Toast.makeText(context, "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
        }
    }



    override suspend fun uploadPlayerPhoto(uri: Uri, playerId: Int): Result<Uri> {
        try{
            // Obtenemos el resolver de MediaStore
            val resolver = context.contentResolver
            // Abrimos el input stream a partir de la URI
            val inputStream = resolver.openInputStream(uri)
                ?: throw IllegalArgumentException("Cannot open InputStream from Uri")
            // Obtenemos el tipo del fichero
            val mimeType = resolver.getType(uri) ?: "image/*"
            // Obtenemos el nombre local, esto podiamos cambiarlo a otro patrón
            val fileName = uri.lastPathSegment ?: "evidence.jpg"
            // Convertimos el fichero a cuerpo de la petición
            val requestBody = inputStream.readBytes().toRequestBody(mimeType.toMediaTypeOrNull())


            // Construimos la parte de la petición
            val part = MultipartBody.Part.createFormData("files", fileName, requestBody)
            // Map con el resto de parámetros
            val partMap: MutableMap<String, RequestBody> = mutableMapOf()

            // Referencia
            partMap["ref"] = "api::player.player".toRequestBody("text/plain".toMediaType())
            // Id del incidente
            partMap["refId"] = playerId.toString().toRequestBody("text/plain".toMediaType())
            // Campo de la colección
            partMap["field"] = "playerProfilePhoto".toRequestBody("text/plain".toMediaType())

            // Subimos el fichero
            val imageResponse = remoteData.uploadImg(
                partMap,
                filepart = part
            )
            // Si ha ido mal la subida, salimos con error
            if (!imageResponse.isSuccessful) {
                return Result.failure(Exception("Error al subir imagen"))
            }
            else {
                val remoteUri= "${NetworkModule.STRAPI}${imageResponse.body()!!.first().formats.small.url}"
                return Result.success(remoteUri.toUri())
            }

        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

}