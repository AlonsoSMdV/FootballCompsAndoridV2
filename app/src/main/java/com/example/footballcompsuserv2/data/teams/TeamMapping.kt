package com.example.footballcompsuserv2.data.teams

import com.example.footballcompsuserv2.data.local.entities.TeamEntity
import com.example.footballcompsuserv2.data.remote.teams.TeamRaw
import com.example.footballcompsuserv2.data.teams.Team

fun TeamRaw.toExternal(): Team {
    return Team(
        id = this.id.toString(),
        name = this.attributes.name,
        isFavourite = this.attributes.isFavourite,
        tLogo = this.attributes.teamLogo?.data?.attributes?.formats?.small?.url ?:"",
        comId = this.attributes.league?.data?.id.toString(),
        nPlayers = this.attributes.numberOfPlayers
    )
}
fun List<TeamRaw>.toExternal():List<Team> = map(TeamRaw::toExternal)

fun Team.toLocal(): TeamEntity {
    return TeamEntity(
        id = this.id.toInt(),
        name = this.name,
        isFavourite = this.isFavourite,
        tLogo = this.tLogo,
        comId = this.comId,
        nPlayers = this.nPlayers
    )
}
fun List<Team>.toLocal():List<TeamEntity> = map{it.toLocal()}

fun TeamEntity.localToExternal(): Team {
    return Team(
        id = this.id.toString(),
        name = this.name,
        isFavourite = this.isFavourite,
        tLogo = this.tLogo,
        comId = this.comId,
        nPlayers = this.nPlayers
    )
}
fun List<TeamEntity>.localToExternal():List<Team> = map{it.localToExternal()}