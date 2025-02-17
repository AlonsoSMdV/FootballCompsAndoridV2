package com.example.footballcompsuserv2.data.local.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.footballcompsuserv2.data.local.entities.TeamEntity
import kotlinx.coroutines.flow.Flow

interface TeamDao {
    @Query("SELECT * FROM teams WHERE comId = :id")
    suspend fun getTeamsByLeague(id: Int): Flow<List<TeamEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createTeam(teamEntity: TeamEntity)

    @Delete
    suspend fun deleteTeam(teamEntity: TeamEntity)
}