package com.example.footballcompsuserv2.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val firstSurname: String,
    val secondSurname: String?,
    val birthdate: String,
    val nationality: String,
    val dorsal: Int,
    val position: String,
    val isFavourite: Boolean,
    val photo: String?,
    val teamId: String
)
