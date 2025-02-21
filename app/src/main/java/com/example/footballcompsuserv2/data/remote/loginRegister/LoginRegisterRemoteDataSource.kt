package com.example.footballcompsuserv2.data.remote.loginRegister

import com.example.footballcompsuserv2.data.remote.FootballApi

import retrofit2.Response

import javax.inject.Inject

//Clase para hacer el login y register de jugadores
class LoginRegisterRemoteDataSource @Inject constructor(
    private val footballApi: FootballApi
): ILoginRegisterRemoteDataSource {
    //LOGIN
    override suspend fun login(loginUser: LoginRaw): Response<LoginRegisterResponse> {
        return footballApi.login(loginUser)
    }

    //REGISTER
    override suspend fun register(registerUser: RegisterRaw): Response<LoginRegisterResponse> {
        return footballApi.register(registerUser)
    }
}