package com.example.footballcompsuserv2.data.remote.leagues

import com.example.footballcompsuserv2.data.leagues.Competition
import retrofit2.Response

interface ICompRemoteDataSource {
    suspend fun readAll(): Response<CompListRaw>
    suspend fun readFavs(filters: Map<String, String>): Response<CompListRaw>
    suspend fun readOne(id:Int): Response<Competition>
    suspend fun createComp(comp: CompCreate)
    suspend fun deleteComp(id: Int)
}