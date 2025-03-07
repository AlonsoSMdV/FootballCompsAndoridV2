package com.example.footballcompsuserv2.di

import android.content.Context
import android.content.SharedPreferences

import com.example.footballcompsuserv2.data.local.FootballCompsDataBase
import com.example.footballcompsuserv2.data.local.ILocalDataSource
import com.example.footballcompsuserv2.data.local.LocalDataSource
import com.example.footballcompsuserv2.data.local.dao.LeagueDao
import com.example.footballcompsuserv2.data.local.dao.MatchDao
import com.example.footballcompsuserv2.data.local.dao.PlayerDao
import com.example.footballcompsuserv2.data.local.dao.TeamDao
import com.example.footballcompsuserv2.data.local.dao.UserDao

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

import javax.inject.Singleton

//Providers de local
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): FootballCompsDataBase {
        return FootballCompsDataBase.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideLeagueDao(database: FootballCompsDataBase): LeagueDao {
        return database.leagueDao()
    }

    @Singleton
    @Provides
    fun provideTeamDao(database: FootballCompsDataBase): TeamDao {
        return database.teamDao()
    }

    @Singleton
    @Provides
    fun providePlayerDao(database: FootballCompsDataBase): PlayerDao {
        return database.playerDao()
    }

    @Singleton
    @Provides
    fun provideMatchDao(database: FootballCompsDataBase): MatchDao {
        return database.matchDao()
    }

    @Singleton
    @Provides
    fun provideUserDao(database: FootballCompsDataBase): UserDao {
        return database.userDao()
    }

    @Singleton
    @Provides
    fun provideLocalDataSource(
        leagueDao: LeagueDao,
        teamDao: TeamDao,
        playerDao: PlayerDao,
        matchDao: MatchDao,
        userDao: UserDao,
        sharedPreferences: SharedPreferences
    ): ILocalDataSource{
        return LocalDataSource(leagueDao, teamDao, playerDao, matchDao, userDao, sharedPreferences)
    }

    @Singleton
    @Provides
    fun provideNetworkUtils(context: Context): NetworkUtils{
        return NetworkUtils(context)
    }
}