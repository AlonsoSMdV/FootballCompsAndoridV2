package com.example.footballcompsuserv2.data.players

data class Player (
    val id: String,
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