package com.example.footballcompsuserv2.data.teams

import com.example.footballcompsuserv2.data.remote.teams.TeamCreate
import kotlinx.coroutines.flow.StateFlow
import retrofit2.http.QueryMap

interface ITeamRepository {
    val setStream: StateFlow<List<Team>>
    suspend fun readAll(): List<Team>
    suspend fun readFavs(isFav: Boolean): List<Team>
    suspend fun readTeamsByLeague(leagueId: Int): List<Team>
    suspend fun readOne(id: Int): Team
    suspend fun createTeam(team: TeamCreate)
    suspend fun deleteTeam(id: Int)
    suspend fun updateTeam(id: Int, team: TeamCreate)
}