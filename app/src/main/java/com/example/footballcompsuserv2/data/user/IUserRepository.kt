package com.example.footballcompsuserv2.data.user

import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.flow.StateFlow

interface IUserRepository {
    val setStream: StateFlow<List<User>>
    val setStreamFb: StateFlow<List<UserFb>>
    suspend fun getActualUser(): User
    suspend fun getActualUserFb(): UserFb
    suspend fun updateUserLeagueFav(leagueRef: DocumentReference)
}