package com.example.footballcompsuserv2.data.matches

import kotlinx.coroutines.flow.StateFlow

interface IMatchRepository {
    val setStream: StateFlow<List<Match>>
    suspend fun readAll(): List<Match>
}