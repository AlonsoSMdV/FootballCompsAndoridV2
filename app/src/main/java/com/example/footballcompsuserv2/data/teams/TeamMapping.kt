package com.example.footballcompsuserv2.data.teams

import com.example.footballcompsuserv2.data.remote.teams.TeamRaw
import com.example.footballcompsuserv2.data.teams.Team

fun TeamRaw.toExternal(): Team {
    return Team(
        id = this.id.toString(),
        name = this.attributes.name,
        comId = this.attributes.league.toString()
    )
}
fun List<TeamRaw>.toExternal():List<Team> = map(TeamRaw::toExternal)