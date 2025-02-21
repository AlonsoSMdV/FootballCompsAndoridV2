package com.example.footballcompsuserv2.di

import com.example.footballcompsuserv2.data.remote.user.IUserRemoteDataSource
import com.example.footballcompsuserv2.data.remote.user.UserRemoteDataSource
import com.example.footballcompsuserv2.data.user.IUserRepository
import com.example.footballcompsuserv2.data.user.UserRepository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserModule {
    @Singleton
    @Binds
    abstract fun bindsUserRepository(repository: UserRepository): IUserRepository

    @Singleton
    @Binds
    abstract fun bindsUserRemote(remote: UserRemoteDataSource): IUserRemoteDataSource
}