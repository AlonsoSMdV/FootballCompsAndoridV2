package com.example.footballcompsuserv2.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teams")
data class TeamEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val isFavourite: Boolean,
    val nPlayers: Int,
    val tLogo: String?,
    val comId: String
)
