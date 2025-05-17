package com.example.footballcompsuserv2.data.matches

import kotlinx.coroutines.flow.StateFlow

interface IMatchRepository {
    val setStream: StateFlow<List<Match>>
    val setStreamFb: StateFlow<List<MatchFbWithTeams>>
    suspend fun readAll(): List<Match>
    suspend fun getMatchesFb(): List<MatchFbWithTeams>
    suspend fun getMatchesFbById(id: String): MatchFbWithTeams
    suspend fun updateMatchStatuses()
}