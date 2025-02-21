package com.example.footballcompsuserv2.data.remote.teams


import com.example.footballcompsuserv2.data.remote.FootballApi
import com.example.footballcompsuserv2.data.remote.uploadImg.CreatedMediaItemResponse
import com.example.footballcompsuserv2.data.remote.uploadImg.StrapiResponse

import okhttp3.MultipartBody
import okhttp3.RequestBody

import retrofit2.Response

import javax.inject.Inject

//Clase para obtener, añadir, modificar y borrar datos del remoto de equipos
class TeamRemoteDataSource @Inject constructor(
    private val teamApi: FootballApi
): ITeamRemoteDataSource {

    //OBTENER
    override suspend fun readAll(): Response<TeamListRaw> {
        return teamApi.getTeams()
    }

    override suspend fun readFavs(filters: Map<String, Boolean>): Response<TeamListRaw> {
        return teamApi.getFavTeams(filters)
    }

    override suspend fun readTeamsByLeague(filters: Map<String, String>): Response<TeamListRaw> {
        return teamApi.getTeamsByLeague(filters)
    }

    override suspend fun readOne(id: Int): Response<TeamResponse> {
        return teamApi.getOneTeam(id)
    }

    //AÑADIR
    override suspend fun createTeam(team: TeamCreate): Response<StrapiResponse<TeamRaw>> {
        return teamApi.createTeam(team)
    }

    //BORRAR
    override suspend fun deleteTeam(id: Int) {
        return teamApi.deleteTeam(id)
    }

    //MODIFICAR
    override suspend fun updateTeam(id: Int, team: TeamUpdate) {
        return teamApi.updateTeam(id, team)
    }

    //SUBIR IMAGENES
    override suspend fun uploadImg(
        partMap: MutableMap<String, RequestBody>,
        filepart: MultipartBody.Part
    ): Response<List<CreatedMediaItemResponse>> {
        return  teamApi.addPhoto(partMap, filepart)
    }


}