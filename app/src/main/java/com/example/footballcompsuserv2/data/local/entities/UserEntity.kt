package com.example.footballcompsuserv2.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Int,
    val userName: String,
    val email: String,
    val token: String?
)