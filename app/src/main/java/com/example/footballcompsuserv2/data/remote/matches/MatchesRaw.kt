package com.example.footballcompsuserv2.data.remote.matches



class MatchesRaw (
    val id: Int,
    val attributes: MatchesRawAttributes
)

data class MatchesRawAttributes(
    val day: String,
    val hour: String,
    val result: String?,
    val place: String,
    val local: TeamRelation?,
    val visitor: TeamRelation?
)
data class TeamRelation(
    val data: TeamData?
)

data class TeamData(
    val id: Int,
    val attributes: TeamRelAtts?
)

data class TeamRelAtts(
    val name: String,
    val teamLogo: TeamRelLogo?
)

data class TeamRelLogo(
    val data: TRLogo?
)

data class TRLogo(
    val id: Int,
    val attributes: RelAtts?
)
data class RelAtts(
    val formats: FormatRel
)
data class FormatRel(
    val small: RelLogoDetail
)

data class RelLogoDetail(
    val url: String
)


data class MatchesCreate(val data: MatchesRawAttributes)
data class MatchesResponse(val data: MatchesRaw)