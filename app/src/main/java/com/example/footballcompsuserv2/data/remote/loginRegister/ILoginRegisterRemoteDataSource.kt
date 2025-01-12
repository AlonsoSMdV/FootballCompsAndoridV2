package com.example.footballcompsuserv2.data.remote.loginRegister

import retrofit2.Response

interface ILoginRegisterRemoteDataSource {
    suspend fun login(loginUser: LoginRaw): Response<LoginRegisterResponse>
    suspend fun register(registerUser: RegisterRaw): Response<LoginRegisterResponse>
}