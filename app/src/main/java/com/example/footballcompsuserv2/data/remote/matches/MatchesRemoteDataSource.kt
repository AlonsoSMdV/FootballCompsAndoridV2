package com.example.footballcompsuserv2.data.remote.matches

import com.example.footballcompsuserv2.data.remote.FootballApi

import retrofit2.Response

import javax.inject.Inject

//Clase para obtener datos del remoto de partidos
class MatchesRemoteDataSource @Inject constructor(
    private val matchApi: FootballApi
): IMatchesRemoteDataSource{
    //OBTENER
    override suspend fun readAll(): Response<MatchesListRaw> {
        return matchApi.getMatches()
    }
}