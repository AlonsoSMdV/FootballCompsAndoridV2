package com.example.footballcompsuserv2.data.user

import kotlinx.coroutines.flow.StateFlow

interface IUserRepository {
    val setStream: StateFlow<List<User>>
    suspend fun getActualUser(): User
}