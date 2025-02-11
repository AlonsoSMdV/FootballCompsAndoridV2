package com.example.footballcompsuserv2.data.players

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.example.footballcompsuserv2.data.remote.players.IPlayerRemoteDataSource
import com.example.footballcompsuserv2.data.remote.players.PlayerCreate
import com.example.footballcompsuserv2.data.remote.players.PlayerRaw
import com.example.footballcompsuserv2.data.remote.players.PlayerResponse
import com.example.footballcompsuserv2.data.remote.players.PlayerUpdate
import com.example.footballcompsuserv2.data.remote.uploadImg.StrapiResponse
import com.example.footballcompsuserv2.di.NetworkModule
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import javax.inject.Inject

class PlayerRepository @Inject constructor(
    private val remoteData: IPlayerRemoteDataSource,
    @ApplicationContext private val context: Context,
): IPlayerRepository {

    private val _state = MutableStateFlow<List<Player>>(listOf())
    override val setStream: StateFlow<List<Player>>
        get() = _state.asStateFlow()

    override suspend fun readAll(): List<Player> {
        val res = remoteData.readAll()
        val players = _state.value.toMutableList()
        if (res.isSuccessful){
            val playerList = res.body()?.data ?: emptyList()
            _state.value = playerList.toExternal()
        }
        else _state.value = players
        return players
    }

    override suspend fun readFavs(isFav: Boolean): List<Player> {
        val filters = mapOf(
            "filters[isFavourite][\$eq]" to isFav
        )
        val res = remoteData.readFavs(filters)
        if (res.isSuccessful) {
            val playerList = res.body()?.data ?: emptyList()
            _state.value = playerList.toExternal()
            return _state.value.filter { it.isFavourite } // Doble verificación en el cliente
        }
        return emptyList()
    }

    override suspend fun readPlayersByTeam(teamId: Int): List<Player> {
        val filters = mapOf(
            "filters[team][id][\$eq]" to teamId.toString()
        )
        val res = remoteData.readPlayersByTeam(filters)
        val players = _state.value.toMutableList()
        if (res.isSuccessful){
            val playerList = res.body()?.data ?: emptyList()
            _state.value = playerList.toExternal()
        }
        else _state.value = players
        return players
    }

    override suspend fun readOne(id: Int): Player {

        val res = remoteData.readOne(id)

        return if (res.isSuccessful) {
            val player = res.body()?.data?.toExternal()
                ?: Player("0", "", "", "", "", "", 0 ,"", false, null, "")
            player
        } else {
           Player("0", "", "", "", "", "", 0 ,"", false, null, "")
        }
    }

    override suspend fun createPlayer(player: PlayerCreate, photo: Uri?): Response<StrapiResponse<PlayerRaw>> {
        val response = remoteData.createPlayer(player)
        if(response.isSuccessful){
            var uploadedPlayer = response.body()

            photo?.let { uri ->
                uploadPlayerPhoto(uri, uploadedPlayer!!.data.id)
            }
        }
        return response
    }

    override suspend fun deletePlayer(id: Int) {
        remoteData.deletePlayer(id)
    }

    override suspend fun updatePlayer(id: Int, player: PlayerUpdate) {
        remoteData.updatePlayer(id, player)
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