package com.example.footballcompsuserv2.data.remote.players

import com.example.footballcompsuserv2.data.remote.uploadImg.CreatedMediaItemResponse
import com.example.footballcompsuserv2.data.remote.uploadImg.StrapiResponse

import okhttp3.MultipartBody
import okhttp3.RequestBody

import retrofit2.Response

interface IPlayerRemoteDataSource {
    suspend fun readAll(): Response<PlayerListRaw>
    suspend fun readFavs(filters: Map<String, Boolean>): Response<PlayerListRaw>
    suspend fun readPlayersByTeam(filters: Map<String, String>): Response<PlayerListRaw>
    suspend fun readOne(id:Int): Response<PlayerResponse>
    suspend fun createPlayer(player: PlayerCreate): Response<StrapiResponse<PlayerRaw>>
    suspend fun deletePlayer(id: Int)
    suspend fun updatePlayer(id: Int, player: PlayerUpdate)
    suspend fun uploadImg(partMap: MutableMap<String, RequestBody>, filepart: MultipartBody.Part): Response<List<CreatedMediaItemResponse>>
}