package com.example.footballcompsuserv2.data.teams

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast

import androidx.core.net.toUri

import com.example.footballcompsuserv2.data.local.ILocalDataSource
import com.example.footballcompsuserv2.data.remote.teams.ITeamRemoteDataSource
import com.example.footballcompsuserv2.data.remote.teams.TeamCreate
import com.example.footballcompsuserv2.data.remote.teams.TeamRaw
import com.example.footballcompsuserv2.data.remote.teams.TeamUpdate
import com.example.footballcompsuserv2.data.remote.uploadImg.StrapiResponse
import com.example.footballcompsuserv2.di.NetworkModule
import com.example.footballcompsuserv2.di.NetworkUtils
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

import dagger.hilt.android.qualifiers.ApplicationContext

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody.Companion.toResponseBody

import retrofit2.Response

import javax.inject.Inject

//Clase que obtiene, crea, actualiza o borra los datos del equipo ya sea por remoto o local(si no hay red)
class TeamRepository @Inject constructor(
    private val remoteData: ITeamRemoteDataSource,
    private val local: ILocalDataSource, // Fuente de datos local
    private val networkUtils: NetworkUtils, // Verifica la conexión de red
    @ApplicationContext private val context: Context
) : ITeamRepository {

    private val _state = MutableStateFlow<List<Team>>(listOf())
    override val setStream: StateFlow<List<Team>>
        get() = _state.asStateFlow()

    private val _stateFb = MutableStateFlow<List<TeamFb>>(listOf())
    override val setStreamFb: StateFlow<List<TeamFb>>
        get() = _stateFb.asStateFlow()

    //OBTENER todos los datos
    override suspend fun readAll(): List<Team> {
        return if (networkUtils.isNetworkAvailable()) {//Si hay red los trae en remoto y los guarda en local
            val res = remoteData.readAll()
            if (res.isSuccessful) {
                val teamList = res.body()?.data ?: emptyList()
                _state.value = teamList.toExternal()

                // Guardar en local
                teamList.forEach { team ->
                    local.createLocalTeam(team.toExternal().toLocal())
                }
                teamList.toExternal()
            } else {
                _state.value
            }
        } else {//Si no da un mensaje de no hay conexión
            Toast.makeText(context, "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
            _state.value
        }
    }

    //OBTENER solo los equipos favoritos
    override suspend fun readFavs(isFav: Boolean): List<Team> {
        return if (networkUtils.isNetworkAvailable()) {//Si hay red los trae en remoto
            val filters = mapOf(
                "filters[isFavourite][\$eq]" to isFav
            )
            val res = remoteData.readFavs(filters)
            if (res.isSuccessful) {
                val teamList = res.body()?.data ?: emptyList()
                _state.value = teamList.toExternal()
                return _state.value.filter { it.isFavourite }
            }
            emptyList()
        } else {//Si no los trae del local
            local.getFavLocalTeams().collect { localTeams ->
                _state.value = localTeams.localToExternal().filter { it.isFavourite }
            }
            _state.value.filter { it.isFavourite }
        }
    }

    //OBTENER los jugadores por la liga elegido
    override suspend fun readTeamsByLeague(leagueId: Int): List<Team> {//Si hay red trae los datos en remoto y los guarda al local
        return if (networkUtils.isNetworkAvailable()) {
            val filters = mapOf(
                "filters[league][id][\$eq]" to leagueId.toString()
            )
            val res = remoteData.readTeamsByLeague(filters)
            if (res.isSuccessful) {
                val teamList = res.body()?.data ?: emptyList()
                _state.value = teamList.toExternal()
                // Guardar en local
                teamList.forEach { team ->
                    local.createLocalTeam(team.toExternal().toLocal())
                }
                return _state.value
            }
            _state.value
        } else {//Si no los trae del local
            local.getLocalTeamsByLeague(leagueId).collect { localTeams ->
                _state.value = localTeams.localToExternal()
            }
            _state.value
        }
    }

    //OBTENER un equipo elegido
    override suspend fun readOne(id: Int): Team {
        return if (networkUtils.isNetworkAvailable()) {//Si hay red trae los datos del remoto
            val res = remoteData.readOne(id)
            res.body()?.data?.toExternal() ?: Team("0", "", false, 0, null, "")
        } else {//Si no da mensaje de no hay conexion
            Toast.makeText(context, "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
            Team("0", "", false, 0, null, "")
        }
    }

    //AÑADIR equipo a la base de datos
    override suspend fun createTeam(team: TeamCreate, logo: Uri?): Response<StrapiResponse<TeamRaw>> {
        return if (networkUtils.isNetworkAvailable()) {//Si hay red lo añade en remoto
            val response = remoteData.createTeam(team)
            if (response.isSuccessful) {
                val uploadedTeam = response.body()
                logo?.let { uri -> uploadTeamLogo(uri, uploadedTeam!!.data.id) }
            }
            response
        } else {//Si no da mensaje de no hay conexion
            Toast.makeText(context, "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
            Response.error(400, "No hay conexión".toResponseBody(null)) // Devolver error simulado
        }
    }

    //BORRAR equipo de la base de datos
    override suspend fun deleteTeam(id: Int) {
        if (networkUtils.isNetworkAvailable()) {//Si hay red lo borra del remoto
            remoteData.deleteTeam(id)
        } else {//Si no da mensaje de no hay conexion
            Toast.makeText(context, "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
        }
    }

    //MODIFICAR equipo de la base de datos
    override suspend fun updateTeam(id: Int, team: TeamUpdate) {
        if (networkUtils.isNetworkAvailable()) {//Si hay red lo modifica en remoto
            remoteData.updateTeam(id, team)
        } else {//Si no da mensaje de no hay conexion
            Toast.makeText(context, "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
        }
    }

    //SUBIR IMAGENES a la base de datos
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

    //Firebase
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    override suspend fun getTeamsByleagueFb(compId: String): List<TeamFb> {
        return try {
            // Primero creamos una referencia al documento de la liga
            val leagueRef = firestore.collection("leagues").document(compId)

            // Ahora consultamos los equipos que tengan esta referencia en su campo league
            val snapshot = firestore.collection("teams")
                .whereEqualTo("league", leagueRef)
                .get()
                .await()

            val teamList = snapshot.documents.mapNotNull { doc ->
                val team = doc.toObject(TeamFb::class.java)
                team?.apply { id = doc.id }
            }

            _stateFb.value = teamList
            teamList
        } catch (e: Exception) {
            Log.e("TeamRepository", "Error al obtener equipos: ${e.message}")
            emptyList()
        }
    }

    override suspend fun addTeamFb(team: TeamFbFields, compId: String): Boolean {
        return try {
            val leagueRef = firestore.collection("leagues").document(compId)

            val teamToSave = team.copy(league = leagueRef)

            firestore.collection("teams")
                .add(teamToSave)
                .await()

            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun updateTeamFb(teamId: String, team: TeamFbFields): Boolean {
        return try {
            firestore.collection("teams")
                .document(teamId)
                .set(team)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun deleteTeamFb(id: String): Boolean {
        return try {
            firestore.collection("teams")
                .document(id)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun uploadImageToFirebaseStorage(uri: Uri): String? {
        return try {
            val fileName = "uploads/teams/${System.currentTimeMillis()}.jpg"
            val imageRef = storage.reference.child(fileName)

            imageRef.putFile(uri).await()
            imageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun createTeamWithOptionalImage(team: TeamFbFields, imageUri: Uri?, compId: String): Boolean {
        return try {
            val finalTeam = if (imageUri != null) {
                val imageUrl = uploadImageToFirebaseStorage(imageUri)
                team.copy(picture = imageUrl ?: "")
            } else team

            addTeamFb(finalTeam, compId)
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun updateTeamWithOptionalImage(
        teamId: String,
        updatedData: TeamFbFields,
        imageUri: Uri?
    ): Boolean {
        return try {
            val finalTeam = if (imageUri != null) {
                val imageUrl = uploadImageToFirebaseStorage(imageUri)
                updatedData.copy(picture = imageUrl ?: "")
            } else updatedData

            updateTeamFb(teamId, finalTeam)
        } catch (e: Exception) {
            false
        }
    }
}