package com.example.footballcompsuserv2.di

import com.example.footballcompsuserv2.data.remote.teams.ITeamRemoteDataSource
import com.example.footballcompsuserv2.data.remote.teams.TeamRemoteDataSource
import com.example.footballcompsuserv2.data.teams.ITeamRepository
import com.example.footballcompsuserv2.data.teams.TeamRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TeamsModule {
    @Singleton
    @Binds
    abstract fun bindTeamsRepository(repository: TeamRepository): ITeamRepository

    @Singleton
    @Binds
    abstract fun bindTeamRemote(remote: TeamRemoteDataSource): ITeamRemoteDataSource
}