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
    val isFavourite: Boolean,
    val playerProfilePhoto: LogoWrapper?,
    val team: Int?
)

data class LogoRaw(
    val id: Int,
    val attributes: LogoRawAttributes
)

data class LogoWrapper(
    val data: LogoRaw?
)

data class LogoRawAttributes(
    val name: String,
    val formats: FormatLogo?
)

data class FormatLogo(
    val small: LogoDetail
)

data class LogoDetail(
    val url: String
)


data class PlayerCreate(val data: PlayerRawAttributes)