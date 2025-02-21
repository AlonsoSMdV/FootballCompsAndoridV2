package com.example.footballcompsuserv2.data.remote.teams


import com.example.footballcompsuserv2.data.remote.uploadImg.CreatedMediaItemResponse
import com.example.footballcompsuserv2.data.remote.uploadImg.StrapiResponse

import okhttp3.MultipartBody
import okhttp3.RequestBody

import retrofit2.Response

interface ITeamRemoteDataSource {
    suspend fun readAll(): Response<TeamListRaw>
    suspend fun readFavs(filters: Map<String, Boolean>): Response<TeamListRaw>
    suspend fun readTeamsByLeague(filters: Map<String, String>): Response<TeamListRaw>
    suspend fun readOne(id: Int): Response<TeamResponse>
    suspend fun createTeam(team: TeamCreate): Response<StrapiResponse<TeamRaw>>
    suspend fun deleteTeam(id: Int)
    suspend fun updateTeam(id: Int, team: TeamUpdate)
    suspend fun uploadImg(partMap: MutableMap<String, RequestBody>, filepart: MultipartBody.Part): Response<List<CreatedMediaItemResponse>>

}