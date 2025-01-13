package com.example.footballcompsuserv2.data.players

import com.example.footballcompsuserv2.data.remote.players.PlayerCreate
import kotlinx.coroutines.flow.StateFlow

interface IPlayerRepository {
    val setStream: StateFlow<List<Player>>
    suspend fun readAll(): List<Player>
    suspend fun readPlayersByTeam(teamId: Int): List<Player>
    suspend fun readOne(id: Int): Player
    suspend fun createPlayer(player: PlayerCreate)
    suspend fun deletePlayer(id: Int)
}