package com.example.footballcompsuserv2.data.teams

import com.example.footballcompsuserv2.data.remote.teams.ITeamRemoteDataSource
import com.example.footballcompsuserv2.data.remote.teams.TeamCreate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class TeamRepository @Inject constructor(
    private val remoteData: ITeamRemoteDataSource
): ITeamRepository {
    private val _state = MutableStateFlow<List<Team>>(listOf())
    override val setStream: StateFlow<List<Team>>
        get() = _state.asStateFlow()

    override suspend fun readAll(): List<Team> {
        val res = remoteData.readAll()
        val teams = _state.value.toMutableList()
        if (res.isSuccessful){
            val teamList = res.body()?.data ?: emptyList()
            _state.value = teamList.toExternal()
        }
        else _state.value = teams
        return teams
    }

    override suspend fun readTeamsByLeague(leagueId: Int): List<Team> {
        val filters = mapOf(
            "filters[league][id][\$eq]" to leagueId.toString()
        )
        val res = remoteData.readTeamsByLeague(filters)
        val teams = _state.value.toMutableList()
        if (res.isSuccessful){
            val teamList = res.body()?.data ?: emptyList()
            _state.value = teamList.toExternal()
        }
        else _state.value = teams
        return teams
    }

    override suspend fun readOne(id: Int): Team {
        val res = remoteData.readOne(id)
        return if (res.isSuccessful)res.body()!!
        else Team("0","", "0")
    }

    override suspend fun createTeam(team: TeamCreate) {
        remoteData.createTeam(team)
    }

    override suspend fun deleteTeam(id: Int) {
        remoteData.deleteTeam(id)
    }
}