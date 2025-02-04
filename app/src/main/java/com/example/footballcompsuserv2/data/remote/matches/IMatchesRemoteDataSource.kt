package com.example.footballcompsuserv2.data.remote.matches

import retrofit2.Response

interface IMatchesRemoteDataSource {
    suspend fun readAll(): Response<MatchesListRaw>
}