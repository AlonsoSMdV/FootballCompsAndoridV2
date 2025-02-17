package com.example.footballcompsuserv2.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey val id: Int,
    val day: String,
    val hour: String,
    val result: String?,
    val place : String,
    val local: String?,
    val visitor: String?,
    val localTeamName: String?,
    val visitorTeamName: String?,
    val localTeamImg: String?,
    val visitorTeamImg: String?
)
