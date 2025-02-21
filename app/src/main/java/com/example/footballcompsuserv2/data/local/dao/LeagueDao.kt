package com.example.footballcompsuserv2.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

import com.example.footballcompsuserv2.data.local.entities.LeagueEntity

import kotlinx.coroutines.flow.Flow

//Interfaz con sentencias sql para obtener, añadir y borrar datos de la tabla ligas base de datos local
@Dao
interface LeagueDao {

    //OBTENER
    @Query("SELECT * FROM leagues")
    fun getLocalLeagues(): Flow<List<LeagueEntity>>

    @Query("SELECT * FROM leagues WHERE isFavourite = 1")
    fun getLocalFavsLeagues(): Flow<List<LeagueEntity>>

    //AÑADIR
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createLocalLeague(leagueEntity: LeagueEntity)

    //BORRAR
    @Delete
    suspend fun deleteLocalLeague(leagueEntity: LeagueEntity)
}