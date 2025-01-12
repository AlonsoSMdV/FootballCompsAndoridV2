package com.example.footballcompsuserv2.data.remote.loginRegister

import com.example.footballcompsuserv2.data.remote.FootballApi
import retrofit2.Response
import javax.inject.Inject

class LoginRegisterRemoteDataSource @Inject constructor(
    private val footballApi: FootballApi
): ILoginRegisterRemoteDataSource {
    override suspend fun login(loginUser: LoginRaw): Response<LoginRegisterResponse> {
        return footballApi.login(loginUser)
    }

    override suspend fun register(registerUser: RegisterRaw): Response<LoginRegisterResponse> {
        return footballApi.register(registerUser)
    }
}