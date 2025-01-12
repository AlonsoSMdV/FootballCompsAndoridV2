package com.example.footballcompsuserv2.data.loginRegister

import com.example.footballcompsuserv2.data.remote.loginRegister.ILoginRegisterRemoteDataSource
import com.example.footballcompsuserv2.data.remote.loginRegister.LoginRaw
import com.example.footballcompsuserv2.data.remote.loginRegister.LoginRegisterResponse
import com.example.footballcompsuserv2.data.remote.loginRegister.RegisterRaw
import retrofit2.Response
import javax.inject.Inject

class LoginRegisterRepo @Inject constructor(
    private val remoteLoginRegister: ILoginRegisterRemoteDataSource
): ILoginRegisterRepo {
    override suspend fun login(loginUser: LoginRaw): Response<LoginRegisterResponse> {
        return remoteLoginRegister.login(loginUser)
    }

    override suspend fun register(registerUser: RegisterRaw): Response<LoginRegisterResponse> {
        return remoteLoginRegister.register(registerUser)
    }

}