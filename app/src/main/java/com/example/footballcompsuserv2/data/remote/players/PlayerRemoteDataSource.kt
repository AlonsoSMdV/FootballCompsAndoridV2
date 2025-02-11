package com.example.footballcompsuserv2.data.remote.players

import com.example.footballcompsuserv2.data.players.Player
import com.example.footballcompsuserv2.data.remote.FootballApi
import com.example.footballcompsuserv2.data.remote.uploadImg.CreatedMediaItemResponse
import com.example.footballcompsuserv2.data.remote.uploadImg.StrapiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    override suspend fun readOne(id: Int): Response<PlayerResponse> {
        return  playerApi.getOnePlayer(id)
    }

    override suspend fun createPlayer(player: PlayerCreate): Response<StrapiResponse<PlayerRaw>> {
        return playerApi.createPlayer(player)
    }

    override suspend fun deletePlayer(id: Int) {
        return playerApi.deletePlayer(id)
    }

    override suspend fun updatePlayer(id: Int, player: PlayerUpdate) {
        return playerApi.updatePlayer(id, player)
    }

    override suspend fun uploadImg(
        partMap: MutableMap<String, RequestBody>,
        filepart: MultipartBody.Part
    ): Response<List<CreatedMediaItemResponse>> {
        return playerApi.addPhoto(partMap, filepart)
    }
}