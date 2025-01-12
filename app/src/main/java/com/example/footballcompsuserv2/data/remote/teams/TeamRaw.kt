package com.example.footballcompsuserv2.data.remote.teams

data class TeamRaw(val id: Int,
                   val attributes: TeamRawAttributes
)

data class TeamRawAttributes(val name: String,
                             val numberOfPlayers: Int,
                             val league: Int
)

data class TeamCreate(val data: TeamRawAttributes)
