package com.example.footballcompsuserv2.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

import com.example.footballcompsuserv2.data.local.entities.MatchEntity

import kotlinx.coroutines.flow.Flow

//Interfaz con sentencias sql para obtener, añadir datos de la tabla partidos base de datos local
@Dao
interface MatchDao {

    //OBTENER
    @Query("SELECT * FROM matches")
    fun getLocalMatches(): Flow<List<MatchEntity>>

    //AÑADIR
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createLocalMatch(matchEntity: MatchEntity)
}