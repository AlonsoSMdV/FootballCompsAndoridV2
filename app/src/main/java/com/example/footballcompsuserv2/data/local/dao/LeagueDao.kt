package com.example.footballcompsuserv2.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.footballcompsuserv2.data.local.entities.LeagueEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LeagueDao {

    @Query("SELECT * FROM leagues")
    suspend fun getLeagues(): Flow<List<LeagueEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createLeague(leagueEntity: LeagueEntity)

    @Delete
    suspend fun deleteLeague(leagueEntity: LeagueEntity)
}