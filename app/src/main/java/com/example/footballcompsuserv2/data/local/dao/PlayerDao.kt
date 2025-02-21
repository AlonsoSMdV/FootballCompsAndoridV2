package com.example.footballcompsuserv2.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

import com.example.footballcompsuserv2.data.local.entities.PlayerEntity

import kotlinx.coroutines.flow.Flow

//Interfaz con sentencias sql para obtener, añadir y borrar datos de la tabla jugadores base de datos local
@Dao
interface PlayerDao {

    //OBTENER
    @Query("SELECT * FROM players WHERE teamId = :id")
    fun getLocalPlayersByTeam(id: Int): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players WHERE id = :id")
    fun getLocalOnePlayer(id: Int): PlayerEntity?

    @Query("SELECT * FROM players WHERE isFavourite = 1")
    fun getLocalFavsPlayers(): Flow<List<PlayerEntity>>

    //AÑADIR
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createLocalPlayer(playerEntity: PlayerEntity)

    //BORRAR
    @Delete
    suspend fun deleteLocalPlayer(playerEntity: PlayerEntity)
}