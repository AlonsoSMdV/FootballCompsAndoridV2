package com.example.footballcompsuserv2.data.remote.teams

import com.example.footballcompsuserv2.data.teams.Team
import retrofit2.Response

interface ITeamRemoteDataSource {
    suspend fun readAll(): Response<TeamListRaw>
    suspend fun readFavs(filters: Map<String, Boolean>): Response<TeamListRaw>
    suspend fun readTeamsByLeague(filters: Map<String, String>): Response<TeamListRaw>
    suspend fun readOne(id: Int): Response<Team>
    suspend fun createTeam(team: TeamCreate)
    suspend fun deleteTeam(id: Int)
}