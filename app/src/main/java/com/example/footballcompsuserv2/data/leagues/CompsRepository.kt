package com.example.footballcompsuserv2.data.leagues

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.core.net.toUri
import com.example.footballcompsuserv2.data.local.ILocalDataSource
import com.example.footballcompsuserv2.data.remote.leagues.CompCreate
import com.example.footballcompsuserv2.data.remote.leagues.CompRaw
import com.example.footballcompsuserv2.data.remote.leagues.CompUpdate
import com.example.footballcompsuserv2.data.remote.leagues.ICompRemoteDataSource
import com.example.footballcompsuserv2.data.remote.uploadImg.StrapiResponse
import com.example.footballcompsuserv2.data.teams.toExternal
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

class CompsRepository @Inject constructor(
    private val remoteData: ICompRemoteDataSource,
    private val local: ILocalDataSource, // Agregamos la fuente de datos local
    private val networkUtils: NetworkUtils, // Verifica la conexión de red
    @ApplicationContext private val context: Context
) : ICompsRepository {

    private val _state = MutableStateFlow<List<Competition>>(listOf())
    override val setStream: StateFlow<List<Competition>>
        get() = _state.asStateFlow()

    override suspend fun readAll(): List<Competition> {
        return if (networkUtils.isNetworkAvailable()) {
            val res = remoteData.readAll()
            if (res.isSuccessful) {
                val compList = res.body()?.data ?: emptyList()
                _state.value = compList.toExternal()

                // Guardar las competiciones en la base de datos local
                compList.forEach { comp ->
                    local.createLocalLeague(comp.toExternal().toLocal())
                }

                return compList.toExternal()
            }
            _state.value // Si falla la API, devolver el estado actual
        } else {
            // Cargar datos locales cuando no haya conexión
            local.getLocalLeagues().collect { localComps ->
                _state.value = localComps.localToExternal()
            }
            _state.value
        }
    }

    override suspend fun readFavs(isFav: Boolean): List<Competition> {
        return if (networkUtils.isNetworkAvailable()) {
            val filters = mapOf("filters[isFavourite][\$eq]" to isFav)
            val res = remoteData.readFavs(filters)
            if (res.isSuccessful) {
                val compList = res.body()?.data ?: emptyList()
                _state.value = compList.toExternal()

                // Guardar en la base de datos local las competiciones favoritas
                compList.forEach { comp ->
                    local.createLocalLeague(comp.toExternal().toLocal())
                }

                return _state.value.filter { it.isFavourite }
            }
            emptyList()
        } else {
            // Cargar datos locales cuando no haya conexión
            local.getFavLocalLeagues().collect { localComps ->
                _state.value = localComps.localToExternal().filter { it.isFavourite }
            }
            _state.value.filter { it.isFavourite }
        }
    }

    override suspend fun readOne(id: Int): Competition {
        return if (networkUtils.isNetworkAvailable()) {
            val res = remoteData.readOne(id)
            if (res.isSuccessful) res.body()!!
            else Competition("0", "", "", false)
        } else {
            Toast.makeText(context, "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
            Competition("0", "", "", false)
        }
    }

    override suspend fun createComp(comp: CompCreate, logo: Uri?): Response<StrapiResponse<CompRaw>> {
        return if (networkUtils.isNetworkAvailable()) {
            val response = remoteData.createComp(comp)
            if (response.isSuccessful) {
                val uploadedComp = response.body()
                logo?.let { uri -> uploadLeagueLogo(uri, uploadedComp!!.data.id) }
            }
            response
        } else {
            Toast.makeText(context, "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
            Response.error(400, "No hay conexión".toResponseBody(null)) // Devolver error simulado
        }
    }

    override suspend fun deleteComp(id: Int) {
        if (networkUtils.isNetworkAvailable()) {
            remoteData.deleteComp(id)
        } else {
            Toast.makeText(context, "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
        }
    }

    override suspend fun updateComp(id: Int, comp: CompUpdate) {
        if (networkUtils.isNetworkAvailable()) {
            remoteData.updateComp(id, comp)
        } else {
            Toast.makeText(context, "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
        }
    }

    override suspend fun uploadLeagueLogo(uri: Uri, compId: Int): Result<Uri> {
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
            partMap["ref"] = "api::league.league".toRequestBody("text/plain".toMediaType())
            // Id del incidente
            partMap["refId"] = compId.toString().toRequestBody("text/plain".toMediaType())
            // Campo de la colección
            partMap["field"] = "logo".toRequestBody("text/plain".toMediaType())

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