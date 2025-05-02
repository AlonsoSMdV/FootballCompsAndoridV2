package com.example.footballcompsuserv2.data.matchStatistics

import com.example.footballcompsuserv2.data.players.PlayerFbFields
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface IStatsRepository {
    val setStreamFb: StateFlow<List<StatsFb>>
    suspend fun getStatsByMatch(idMatch: String): StatsFb
    suspend fun addStatFb(statsFb: StatsFbFields, idMatch: String): Boolean

}