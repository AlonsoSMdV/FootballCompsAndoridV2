package com.example.footballcompsuserv2.di

import com.example.footballcompsuserv2.data.leagues.CompsRepository
import com.example.footballcompsuserv2.data.leagues.ICompsRepository
import com.example.footballcompsuserv2.data.firebase.firebaseLeagues.ILeagueFirebase
import com.example.footballcompsuserv2.data.firebase.firebaseLeagues.LeagueFirebase
import com.example.footballcompsuserv2.data.remote.leagues.CompsRemoteDataSource
import com.example.footballcompsuserv2.data.remote.leagues.ICompRemoteDataSource

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class CompsModule {
    @Singleton
    @Binds
    abstract fun bindCompsRepository(repository: CompsRepository): ICompsRepository

    @Singleton
    @Binds
    abstract fun bindCompRemote(remote: CompsRemoteDataSource): ICompRemoteDataSource
}