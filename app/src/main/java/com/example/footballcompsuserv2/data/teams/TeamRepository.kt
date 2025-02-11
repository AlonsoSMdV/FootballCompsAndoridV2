package com.example.footballcompsuserv2.data.teams

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.example.footballcompsuserv2.data.remote.teams.ITeamRemoteDataSource
import com.example.footballcompsuserv2.data.remote.teams.TeamCreate
import com.example.footballcompsuserv2.data.remote.teams.TeamRaw
import com.example.footballcompsuserv2.data.remote.teams.TeamUpdate
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

class TeamRepository @Inject constructor(
    private val remoteData: ITeamRemoteDataSource,
    @ApplicationContext private val context: Context,
): ITeamRepository {
    private val _state = MutableStateFlow<List<Team>>(listOf())
    override val setStream: StateFlow<List<Team>>
        get() = _state.asStateFlow()

    override suspend fun readAll(): List<Team> {
        val res = remoteData.readAll()
        val teams = _state.value.toMutableList()
        if (res.isSuccessful){
            val teamList = res.body()?.data ?: emptyList()
            _state.value = teamList.toExternal()
        }
        else _state.value = teams
        return teams
    }

    override suspend fun readFavs(isFav: Boolean): List<Team> {
        val filters = mapOf(
            "filters[isFavourite][\$eq]" to isFav
        )
        val res = remoteData.readFavs(filters)
        if (res.isSuccessful) {
            val teamList = res.body()?.data ?: emptyList()
            _state.value = teamList.toExternal()
            return _state.value.filter { it.isFavourite } // Doble verificación en el cliente
        }
        return emptyList()
    }

    override suspend fun readTeamsByLeague(leagueId: Int): List<Team> {
        val filters = mapOf(
            "filters[league][id][\$eq]" to leagueId.toString()
        )
        val res = remoteData.readTeamsByLeague(filters)
        val teams = _state.value.toMutableList()
        if (res.isSuccessful){
            val teamList = res.body()?.data ?: emptyList()
            _state.value = teamList.toExternal()
        }
        else _state.value = teams
        return teams
    }

    override suspend fun readOne(id: Int): Team {
        val res = remoteData.readOne(id)
        return if (res.isSuccessful)res.body()?.data?.toExternal()?:Team("0", "", false, 0, null, "")
        else Team("0","", false, 0,null, "")
    }

    override suspend fun createTeam(team: TeamCreate, logo: Uri?): Response<StrapiResponse<TeamRaw>> {
        val response = remoteData.createTeam(team)
        if(response.isSuccessful){
            var uploadedTeam = response.body()

            logo?.let { uri ->
                uploadTeamLogo(uri, uploadedTeam!!.data.id)
            }
        }
        return response
    }

    override suspend fun deleteTeam(id: Int) {
        remoteData.deleteTeam(id)
    }

    override suspend fun updateTeam(id: Int, team: TeamUpdate) {
        remoteData.updateTeam(id, team)
    }

    override suspend fun uploadTeamLogo(uri: Uri, teamId: Int): Result<Uri> {
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
            partMap["ref"] = "api::team.team".toRequestBody("text/plain".toMediaType())
            // Id del incidente
            partMap["refId"] = teamId.toString().toRequestBody("text/plain".toMediaType())
            // Campo de la colección
            partMap["field"] = "teamLogo".toRequestBody("text/plain".toMediaType())

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