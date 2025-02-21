package com.example.footballcompsuserv2.data.loginRegister

import com.example.footballcompsuserv2.data.remote.loginRegister.LoginRaw
import com.example.footballcompsuserv2.data.remote.loginRegister.LoginRegisterResponse
import com.example.footballcompsuserv2.data.remote.loginRegister.RegisterRaw

import retrofit2.Response

interface ILoginRegisterRepo {
    suspend fun login(loginUser: LoginRaw): Response<LoginRegisterResponse>
    suspend fun register(registerUser: RegisterRaw): Response<LoginRegisterResponse>
}