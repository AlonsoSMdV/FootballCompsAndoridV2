package com.example.footballcompsuserv2.data.teams

import android.net.Uri
import com.example.footballcompsuserv2.data.players.PlayerFb

import com.example.footballcompsuserv2.data.remote.teams.TeamCreate
import com.example.footballcompsuserv2.data.remote.teams.TeamRaw
import com.example.footballcompsuserv2.data.remote.teams.TeamUpdate
import com.example.footballcompsuserv2.data.remote.uploadImg.StrapiResponse

import kotlinx.coroutines.flow.StateFlow

import retrofit2.Response

interface ITeamRepository {
    val setStream: StateFlow<List<Team>>
    val setStreamFb: StateFlow<List<TeamFb>>
    suspend fun readAll(): List<Team>
    suspend fun readFavs(isFav: Boolean): List<Team>
    suspend fun readTeamsByLeague(leagueId: Int): List<Team>
    suspend fun readOne(id: Int): Team
    suspend fun createTeam(team: TeamCreate, logo: Uri?): Response<StrapiResponse<TeamRaw>>
    suspend fun deleteTeam(id: Int)
    suspend fun updateTeam(id: Int, team: TeamUpdate)
    suspend fun uploadTeamLogo(uri: Uri, teamId: Int): Result<Uri>
    // Firebase
    suspend fun getTeamsByleagueFb(compId: String): List<TeamFb>
    suspend fun addTeamFb(team: TeamFbFields, compId: String): Boolean
    suspend fun updateTeamFb(teamId: String, team: TeamFbFields): Boolean
    suspend fun deleteTeamFb(id: String): Boolean
    suspend fun uploadImageToFirebaseStorage(uri: Uri): String?
    suspend fun createTeamWithOptionalImage(team: TeamFbFields, imageUri: Uri?, compId: String): Boolean
    suspend fun updateTeamWithOptionalImage(teamId: String, updatedData: TeamFbFields, imageUri: Uri?): Boolean
}