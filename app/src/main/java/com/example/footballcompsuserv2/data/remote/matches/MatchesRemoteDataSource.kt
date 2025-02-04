package com.example.footballcompsuserv2.data.remote.matches

import com.example.footballcompsuserv2.data.remote.FootballApi
import retrofit2.Response
import javax.inject.Inject

class MatchesRemoteDataSource @Inject constructor(
    private val matchApi: FootballApi
): IMatchesRemoteDataSource{
    override suspend fun readAll(): Response<MatchesListRaw> {
        return matchApi.getMatches()
    }
}