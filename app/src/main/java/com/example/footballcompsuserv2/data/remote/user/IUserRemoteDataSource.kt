package com.example.footballcompsuserv2.data.remote.user

import retrofit2.Response

interface IUserRemoteDataSource {
    suspend fun getActualUser(): Response<UserRaw>
}