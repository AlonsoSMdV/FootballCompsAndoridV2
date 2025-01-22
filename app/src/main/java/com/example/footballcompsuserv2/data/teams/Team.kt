package com.example.footballcompsuserv2.data.teams

data class Team (
    val id: String,
    val name: String,
    val isFavourite: Boolean,
    val nPlayers: Int,
    val tLogo: String?,
    val comId: String
)