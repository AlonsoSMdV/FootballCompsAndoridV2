package com.example.footballcompsuserv2.di

import com.example.footballcompsuserv2.data.matchStatistics.IStatsRepository
import com.example.footballcompsuserv2.data.matchStatistics.StatsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StatsModule {
    @Singleton
    @Binds
    abstract fun bindStatsRepository(repository: StatsRepository): IStatsRepository
}