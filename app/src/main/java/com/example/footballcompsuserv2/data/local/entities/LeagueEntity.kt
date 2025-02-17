package com.example.footballcompsuserv2.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "leagues")
data class LeagueEntity (
    @PrimaryKey val id: Int,
    val name: String,
    val logo: String?,
    val isFavourite: Boolean
)