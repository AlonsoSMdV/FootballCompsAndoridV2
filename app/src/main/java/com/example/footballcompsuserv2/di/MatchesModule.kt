package com.example.footballcompsuserv2.di

import com.example.footballcompsuserv2.data.matches.IMatchRepository
import com.example.footballcompsuserv2.data.matches.MatchRepository
import com.example.footballcompsuserv2.data.remote.matches.IMatchesRemoteDataSource
import com.example.footballcompsuserv2.data.remote.matches.MatchesRemoteDataSource

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MatchesModule {
    @Singleton
    @Binds
    abstract fun bindMatchesRepository(repository: MatchRepository): IMatchRepository

    @Singleton
    @Binds
    abstract fun bindMatchesRemote(remote: MatchesRemoteDataSource): IMatchesRemoteDataSource
}