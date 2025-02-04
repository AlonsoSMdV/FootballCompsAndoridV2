package com.example.footballcompsuserv2.data.matches

data class Match (
    val id: String,
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