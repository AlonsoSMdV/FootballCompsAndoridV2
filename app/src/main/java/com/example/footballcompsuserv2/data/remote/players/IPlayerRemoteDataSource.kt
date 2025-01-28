package com.example.footballcompsuserv2.data.remote.players

import com.example.footballcompsuserv2.data.players.Player
import retrofit2.Response

interface IPlayerRemoteDataSource {
    suspend fun readAll(): Response<PlayerListRaw>
    suspend fun readFavs(filters: Map<String, Boolean>): Response<PlayerListRaw>
    suspend fun readPlayersByTeam(filters: Map<String, String>): Response<PlayerListRaw>
    suspend fun readOne(id:Int): Response<PlayerResponse>
    suspend fun createPlayer(player: PlayerCreate)
    suspend fun deletePlayer(id: Int)
    suspend fun updatePlayer(id: Int, player: PlayerCreate)
}