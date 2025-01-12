package com.example.footballcompsuserv2.di

import com.example.footballcompsuserv2.data.players.IPlayerRepository
import com.example.footballcompsuserv2.data.players.PlayerRepository
import com.example.footballcompsuserv2.data.remote.players.IPlayerRemoteDataSource
import com.example.footballcompsuserv2.data.remote.players.PlayerRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PlayersModule {
    @Singleton
    @Binds
    abstract fun bindPlayerRepository(repository: PlayerRepository): IPlayerRepository

    @Singleton
    @Binds
    abstract fun bindPlayerRemote(remote: PlayerRemoteDataSource): IPlayerRemoteDataSource
}