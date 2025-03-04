package com.example.footballcompsuserv2.data.remote.teams

data class TeamRaw(val id: Int,
                   val attributes: TeamRawAttributesMedia
)

data class TeamRawAttributes(
    val name: String,
    val numberOfPlayers: Int,
    val isFavourite: Boolean,
    val league: LeagueData?
)

data class TeamCreateRawAttributes(
    val name: String,
    val numberOfPlayers: Int,
    val isFavourite: Boolean,
    val league: Int
)

data class TeamRawAttributesMedia(
    val name: String,
    val numberOfPlayers: Int,
    val isFavourite: Boolean,
    val teamLogo: LogoWrapper?,
    val league: LeagueData?
)

data class LeagueData(
    val data: LeagueId
)

data class LeagueId(
    val id: Int
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

data class TeamCreate(val data: TeamCreateRawAttributes)
data class TeamUpdate(val data: TeamRawAttributesMedia)
data class TeamResponse(val data: TeamRaw)
