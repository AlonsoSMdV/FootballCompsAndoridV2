package com.example.footballcompsuserv2.di

import com.example.footballcompsuserv2.data.loginRegister.ILoginRegisterRepo
import com.example.footballcompsuserv2.data.loginRegister.LoginRegisterRepo
import com.example.footballcompsuserv2.data.remote.loginRegister.ILoginRegisterRemoteDataSource
import com.example.footballcompsuserv2.data.remote.loginRegister.LoginRegisterRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LoginRegisterModule{
    @Singleton
    @Binds
    abstract fun bindLoginRegisterRepository(repo: LoginRegisterRepo): ILoginRegisterRepo

    @Singleton
    @Binds
    abstract fun bindLoginRegisterRemoteDataSource(repo: LoginRegisterRemoteDataSource): ILoginRegisterRemoteDataSource
}