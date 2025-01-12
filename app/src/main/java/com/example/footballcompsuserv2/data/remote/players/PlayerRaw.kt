package com.example.footballcompsuserv2.data.remote.players

data class PlayerRaw (
    val id: Int,
    val attributes: PlayerRawAttributes
)

data class PlayerRawAttributes(
    val name: String,
    val firstSurname: String,
    val secondSurname: String?,
    val nationality: String,
    val dorsal: Int,
    val birthdate: String,
    val position: String,
    val team: Int
)

data class PlayerCreate(val data: PlayerRawAttributes)