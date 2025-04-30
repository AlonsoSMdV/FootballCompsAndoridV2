package com.example.footballcompsuserv2.data.players

import android.net.Uri
import com.example.footballcompsuserv2.data.leagues.CompetitionFb

import com.example.footballcompsuserv2.data.remote.players.PlayerCreate
import com.example.footballcompsuserv2.data.remote.players.PlayerRaw
import com.example.footballcompsuserv2.data.remote.players.PlayerUpdate
import com.example.footballcompsuserv2.data.remote.uploadImg.StrapiResponse

import kotlinx.coroutines.flow.StateFlow

import retrofit2.Response

interface IPlayerRepository {
    val setStream: StateFlow<List<Player>>
    val setStreamFb: StateFlow<List<PlayerFb>>
    suspend fun readAll(): List<Player>
    suspend fun readFavs(isFav: Boolean): List<Player>
    suspend fun readPlayersByTeam(teamId: Int): List<Player>
    suspend fun readOne(id: Int): Player
    suspend fun createPlayer(player: PlayerCreate, photo: Uri?): Response<StrapiResponse<PlayerRaw>>
    suspend fun deletePlayer(id: Int)
    suspend fun updatePlayer(id: Int, player: PlayerUpdate)
    suspend fun uploadPlayerPhoto(uri: Uri, playerId: Int): Result<Uri>
    // Firebase
    suspend fun getPlayersByTeamFb(teamId: String): List<PlayerFb>
    suspend fun addPlayerFb(player: PlayerFbFields, teamId: String): Boolean
    suspend fun updatePlayerFb(playerId: String, player: PlayerFbFields): Boolean
    suspend fun deletePlayerFb(id: String): Boolean
    suspend fun uploadImageToFirebaseStorage(uri: Uri): String?
    suspend fun createPlayerWithOptionalImage(player: PlayerFbFields, imageUri: Uri?, teamId: String): Boolean
    suspend fun updatePlayersWithOptionalImage(playerId: String, updatedData: PlayerFbFields, imageUri: Uri?): Boolean
}