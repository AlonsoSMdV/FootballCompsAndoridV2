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
    val local: Int?,
    val visitor: Int?
)


data class MatchesCreate(val data: MatchesRawAttributes)
data class MatchesResponse(val data: MatchesRaw)