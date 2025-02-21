package com.example.footballcompsuserv2.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

import com.example.footballcompsuserv2.data.local.entities.UserEntity

//Interfaz con sentencias sql para obtener, añadir y borrar datos de la tabla usuarios base de datos local
@Dao
interface UserDao {
    //OBTENER
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getLocalUser(userId: Int): UserEntity?

    //AÑADIR
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocalUser(user: UserEntity)

    //BORRAR
    @Query("DELETE FROM users")
    suspend fun clearLocalUsers()
}