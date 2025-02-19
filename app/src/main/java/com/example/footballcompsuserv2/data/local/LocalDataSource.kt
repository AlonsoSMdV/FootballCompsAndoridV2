package com.example.footballcompsuserv2.data.local

import android.content.SharedPreferences
import android.util.Log
import com.example.footballcompsuserv2.data.local.dao.LeagueDao
import com.example.footballcompsuserv2.data.local.dao.MatchDao
import com.example.footballcompsuserv2.data.local.dao.PlayerDao
import com.example.footballcompsuserv2.data.local.dao.TeamDao
import com.example.footballcompsuserv2.data.local.dao.UserDao
import com.example.footballcompsuserv2.data.local.entities.LeagueEntity
import com.example.footballcompsuserv2.data.local.entities.MatchEntity
import com.example.footballcompsuserv2.data.local.entities.PlayerEntity
import com.example.footballcompsuserv2.data.local.entities.TeamEntity
import com.example.footballcompsuserv2.data.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSource @Inject constructor(
    private val leagueDao: LeagueDao,
    private val teamDao: TeamDao,
    private val playerDao: PlayerDao,
    private val matchDao: MatchDao,
    private val userDao: UserDao,
    private val sharedPreferences: SharedPreferences
): ILocalDataSource {
    val userId  = sharedPreferences.getString("USER_ID", null)?.toIntOrNull() ?: 0

    //Leagues
    override suspend fun getLocalLeagues(): Flow<List<LeagueEntity>> {
        return leagueDao.getLocalLeagues()
    }

    override suspend fun getFavLocalLeagues(): Flow<List<LeagueEntity>> {
        return leagueDao.getLocalFavsLeagues()
    }

    override suspend fun createLocalLeague(leagueEntity: LeagueEntity) {
        leagueDao.createLocalLeague(leagueEntity)
        Log.d("Local Repository", "League inserted!!")
    }

    override suspend fun deleteLocalLeague(leagueEntity: LeagueEntity) {
        leagueDao.createLocalLeague(leagueEntity)
        Log.d("Local Repository", "League deleted!!")
    }

    //Teams
    override suspend fun getLocalTeamsByLeague(compId: Int): Flow<List<TeamEntity>> {
        return teamDao.getLocalTeamsByLeague(compId)
    }

    override suspend fun getFavLocalTeams(): Flow<List<TeamEntity>> {
        return teamDao.getLocalFavsTeams()
    }

    override suspend fun createLocalTeam(teamEntity: TeamEntity) {
        teamDao.createLocalTeam(teamEntity)
        Log.d("Local Repository", "Team inserted!!")
    }

    override suspend fun deleteLocalTeam(teamEntity: TeamEntity) {
        teamDao.deleteLocalTeam(teamEntity)
        Log.d("Local Repository", "Team deleted!!")
    }

    //Players
    override suspend fun getLocalPlayersByTeam(teamId: Int): Flow<List<PlayerEntity>> {
        return playerDao.getLocalPlayersByTeam(teamId)
    }

    override suspend fun getLocalOnePlayer(id: Int): PlayerEntity? {
        return playerDao.getLocalOnePlayer(id)
    }

    override suspend fun getFavLocalPlayers(): Flow<List<PlayerEntity>> {
        return playerDao.getLocalFavsPlayers()
    }

    override suspend fun createLocalPlayer(playerEntity: PlayerEntity) {
        playerDao.createLocalPlayer(playerEntity)
        Log.d("Local Repository", "Player inserted!!")
    }

    override suspend fun deleteLocalPlayer(playerEntity: PlayerEntity) {
        playerDao.deleteLocalPlayer(playerEntity)
        Log.d("Local Repository", "Player deleted!!")
    }

    //Matches
    override suspend fun getLocalMatches(): Flow<List<MatchEntity>> {
        return matchDao.getLocalMatches()
    }

    override suspend fun createLocalMatch(matchEntity: MatchEntity) {
        matchDao.createLocalMatch(matchEntity)
        Log.d("Local Repository", "Match created!!")
    }

    //Users
    override suspend fun getLocalUser(userId: Int): UserEntity? {
        return userDao.getLocalUser(userId)
    }

    override suspend fun createLocalUser(userEntity: UserEntity) {
        userDao.insertLocalUser(userEntity)
        Log.d("Local Repository", "User inserted!!")
    }

    override suspend fun deleteLocalUser(userEntity: UserEntity) {
        userDao.clearLocalUsers()
        Log.d("Local Repository", "Users deleted!!")
    }
}