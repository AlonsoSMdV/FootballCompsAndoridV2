package com.example.footballcompsuserv2.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

import com.example.footballcompsuserv2.data.local.entities.TeamEntity

import kotlinx.coroutines.flow.Flow

//Interfaz con sentencias sql para obtener, añadir y borrar datos de la tabla equipos base de datos local
@Dao
interface TeamDao {
    //Obtener
    @Query("SELECT * FROM teams WHERE comId = :id")
    fun getLocalTeamsByLeague(id: Int): Flow<List<TeamEntity>>

    @Query("SELECT * FROM teams WHERE isFavourite = 1")
    fun getLocalFavsTeams(): Flow<List<TeamEntity>>

    //AÑADIR
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createLocalTeam(teamEntity: TeamEntity)

    //BORRAR
    @Delete
    suspend fun deleteLocalTeam(teamEntity: TeamEntity)
}