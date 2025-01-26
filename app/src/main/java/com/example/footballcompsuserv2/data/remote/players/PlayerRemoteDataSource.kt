package com.example.footballcompsuserv2.data.remote.players

import com.example.footballcompsuserv2.data.players.Player
import com.example.footballcompsuserv2.data.remote.FootballApi
import retrofit2.Response
import javax.inject.Inject

class PlayerRemoteDataSource @Inject constructor(
    private val playerApi: FootballApi
): IPlayerRemoteDataSource {
    override suspend fun readAll(): Response<PlayerListRaw> {
        return playerApi.getPlayers()
    }

    override suspend fun readFavs(filters: Map<String, Boolean>): Response<PlayerListRaw> {
        return playerApi.getFavPlayers(filters)
    }

    override suspend fun readPlayersByTeam(filters: Map<String, String>): Response<PlayerListRaw> {
        return playerApi.getPlayersByTeam(filters)
    }

    override suspend fun readOne(id: Int): Response<Player> {
        return  playerApi.getOnePlayer(id)
    }

    override suspend fun createPlayer(player: PlayerCreate) {
        return playerApi.createPlayer(player)
    }

    override suspend fun deletePlayer(id: Int) {
        return playerApi.deletePlayer(id)
    }

    override suspend fun updatePlayer(id: Int, player: PlayerCreate) {
        return playerApi.updatePlayer(id, player)
    }
}