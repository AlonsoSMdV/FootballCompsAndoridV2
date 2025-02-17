package com.example.footballcompsuserv2.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.footballcompsuserv2.data.local.entities.MatchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {

    @Query("SELECT * FROM matches")
    fun getLocalMatches(): Flow<List<MatchEntity>>
}