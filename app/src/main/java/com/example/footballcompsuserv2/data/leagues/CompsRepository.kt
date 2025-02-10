package com.example.footballcompsuserv2.data.leagues

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.example.footballcompsuserv2.data.remote.leagues.CompCreate
import com.example.footballcompsuserv2.data.remote.leagues.ICompRemoteDataSource
import com.example.footballcompsuserv2.data.teams.toExternal
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
import javax.inject.Inject

class CompsRepository @Inject constructor(
    private val remoteData: ICompRemoteDataSource,
    @ApplicationContext private val context: Context,
): ICompsRepository {

    private val _state = MutableStateFlow<List<Competition>>(listOf())
    override val setStream: StateFlow<List<Competition>>
        get() = _state.asStateFlow()

    override suspend fun readAll(): List<Competition> {
        val res = remoteData.readAll()
        val comps = _state.value.toMutableList()
        if (res.isSuccessful){
            val compList = res.body()?.data ?: emptyList()
            _state.value = compList.toExternal()
        }
        else _state.value = comps
        return comps
    }

    override suspend fun readFavs(isFav: Boolean): List<Competition> {
        val filters = mapOf(
            "filters[isFavourite][\$eq]" to isFav
        )
        val res = remoteData.readFavs(filters)
        if (res.isSuccessful) {
            val compList = res.body()?.data ?: emptyList()
            _state.value = compList.toExternal()
            return _state.value.filter { it.isFavourite } // Doble verificación en el cliente
        }
        return emptyList()
    }

    override suspend fun readOne(id: Int): Competition {
        val res = remoteData.readOne(id)
        return if (res.isSuccessful)res.body()!!
        else Competition("0","", "", false)
    }

    override suspend fun createComp(comp: CompCreate) {
        remoteData.createComp(comp)
    }

    override suspend fun deleteComp(id: Int) {
        remoteData.deleteComp(id)
    }

    override suspend fun updateComp(id: Int, comp: CompCreate) {
        remoteData.updateComp(id, comp)
    }

    override suspend fun uploadLeagueLogo(uri: Uri, exerciseId: Int): Result<Uri> {
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
            partMap["ref"] = "api::exercise.exercise".toRequestBody("text/plain".toMediaType())
            // Id del incidente
            partMap["refId"] = exerciseId.toString().toRequestBody("text/plain".toMediaType())
            // Campo de la colección
            partMap["field"] = "photo".toRequestBody("text/plain".toMediaType())

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