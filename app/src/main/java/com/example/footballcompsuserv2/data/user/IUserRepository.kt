package com.example.footballcompsuserv2.data.user

import android.net.Uri
import com.example.footballcompsuserv2.data.leagues.CompetitionFb
import com.example.footballcompsuserv2.data.players.PlayerFb
import com.example.footballcompsuserv2.data.teams.TeamFb
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.flow.StateFlow

interface IUserRepository {
    val setStream: StateFlow<List<User>>
    val setStreamFb: StateFlow<List<UserFb>>
    suspend fun getActualUser(): User
    suspend fun getActualUserFb(): UserFb
    suspend fun updateUserLeagueFav(leagueRef: DocumentReference)
    suspend fun updateUser(userId: String, userFb: UserFb): Boolean
    suspend fun uploadImageToFirebaseStorage(uri: Uri): String?
    suspend fun updateUserWithOptionalImage(userId: String, updatedData: UserFb, imageUri: Uri?): Boolean
    suspend fun getFavoriteTeam(reference: DocumentReference?): TeamFb?
    suspend fun getFavoritePlayer(reference: DocumentReference?): PlayerFb?
    suspend fun getFavoriteLeague(reference: DocumentReference?): CompetitionFb?
}