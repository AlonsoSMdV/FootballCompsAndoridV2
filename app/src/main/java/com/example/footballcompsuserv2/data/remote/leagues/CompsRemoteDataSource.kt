package com.example.footballcompsuserv2.data.remote.leagues

import com.example.footballcompsuserv2.data.leagues.Competition
import com.example.footballcompsuserv2.data.remote.FootballApi
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.PartMap
import javax.inject.Inject

class CompsRemoteDataSource @Inject constructor(
    private val compApi: FootballApi
): ICompRemoteDataSource {
    override suspend fun readAll(): Response<CompListRaw> {
        return compApi.getCompetitions()
    }

    override suspend fun readFavs(filters: Map<String, Boolean>): Response<CompListRaw> {
        return compApi.getFavComps(filters)
    }

    override suspend fun readOne(id: Int): Response<Competition> {
        return compApi.getOneCompetition(id)
    }

    override suspend fun createComp(comp: CompCreate): Response<StrapiResponse<CompRaw>> {
        return compApi.createComp(comp)
    }

    override suspend fun deleteComp(id: Int) {
        return compApi.deleteComp(id)
    }

    override suspend fun updateComp(id: Int, comp: CompUpdate) {
        return compApi.updateComp(id, comp)
    }

    override suspend fun uploadImg(partMap: MutableMap<String, RequestBody>, filepart: MultipartBody.Part): Response<List<CreatedMediaItemResponse>> {
        return compApi.addPhoto(partMap, filepart)
    }
}