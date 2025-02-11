package com.example.footballcompsuserv2.data.remote.leagues

import com.example.footballcompsuserv2.data.leagues.Competition
import com.example.footballcompsuserv2.data.remote.uploadImg.CreatedMediaItemResponse
import com.example.footballcompsuserv2.data.remote.uploadImg.StrapiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.PartMap

interface ICompRemoteDataSource {
    suspend fun readAll(): Response<CompListRaw>
    suspend fun readFavs(filters: Map<String, Boolean>): Response<CompListRaw>
    suspend fun readOne(id:Int): Response<Competition>
    suspend fun createComp(comp: CompCreate): Response<StrapiResponse<CompRaw>>
    suspend fun deleteComp(id: Int)
    suspend fun updateComp(id: Int, compCopy: CompUpdate)
    suspend fun uploadImg(partMap: MutableMap<String, RequestBody>, filepart: MultipartBody.Part): Response<List<CreatedMediaItemResponse>>
}