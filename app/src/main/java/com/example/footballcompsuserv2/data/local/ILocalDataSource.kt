package com.example.footballcompsuserv2.data.local

import com.example.footballcompsuserv2.data.local.entities.LeagueEntity
import com.example.footballcompsuserv2.data.local.entities.MatchEntity
import com.example.footballcompsuserv2.data.local.entities.PlayerEntity
import com.example.footballcompsuserv2.data.local.entities.TeamEntity
import com.example.footballcompsuserv2.data.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

interface ILocalDataSource {
    //Leagues
    suspend fun getLocalLeagues(): Flow<List<LeagueEntity>>;
    suspend fun createLocalLeague(leagueEntity: LeagueEntity)
    suspend fun deleteLocalLeague(leagueEntity: LeagueEntity)
    //Teams
    suspend fun getLocalTeamsByLeague(compId: Int): Flow<List<TeamEntity>>;
    suspend fun createLocalTeam(teamEntity: TeamEntity)
    suspend fun deleteLocalTeam(teamEntity: TeamEntity)
    //Players
    suspend fun getLocalPlayersByTeam(teamId: Int): Flow<List<PlayerEntity>>;
    suspend fun createLocalPlayer(playerEntity: PlayerEntity)
    suspend fun deleteLocalPlayer(playerEntity: PlayerEntity)
    //Matches
    suspend fun getLocalMatches(): Flow<List<MatchEntity>>;
    suspend fun createLocalMatch(matchEntity: MatchEntity)
    //Users
    suspend fun getLocalUser(userId: Int): UserEntity?;
    suspend fun createLocalUser(userEntity: UserEntity)
    suspend fun deleteLocalUser(userEntity: UserEntity)
}