package com.example.footballcompsuserv2.data.matches

import com.example.footballcompsuserv2.data.local.entities.MatchEntity
import com.example.footballcompsuserv2.data.remote.matches.MatchesRaw

fun MatchesRaw.toExternal(): Match{
    return Match(
        id = this.id.toString(),
        day = this.attributes.day,
        hour = this.attributes.hour,
        result = this.attributes.result,
        place = this.attributes.place,
        local = this.attributes.local.toString(),
        visitor = this.attributes.visitor.toString(),
        localTeamName = this.attributes.local?.data?.attributes?.name,
        visitorTeamName = this.attributes.visitor?.data?.attributes?.name,
        localTeamImg = this.attributes.local?.data?.attributes?.teamLogo?.data?.attributes?.formats?.small?.url ?: "",
        visitorTeamImg = this.attributes.visitor?.data?.attributes?.teamLogo?.data?.attributes?.formats?.small?.url ?: ""
    )
}

fun List<MatchesRaw>.toExternal(): List<Match> = map ( MatchesRaw::toExternal )

fun Match.toLocal(): MatchEntity{
    return MatchEntity(
        id = this.id.toInt(),
        day = this.day,
        hour = this.hour,
        result = this.result,
        place = this.place,
        local = this.local,
        visitor = this.visitor,
        localTeamName = this.localTeamName,
        visitorTeamName = this.visitorTeamName,
        localTeamImg = this.localTeamImg,
        visitorTeamImg = this.visitorTeamImg
    )
}
fun List<Match>.toLocal(): List<MatchEntity> = map { it.toLocal() }

fun MatchEntity.toExternal(): Match{
    return Match(
        id = this.id.toString(),
        day = this.day,
        hour = this.hour,
        result = this.result,
        place = this.place,
        local = this.local,
        visitor = this.visitor,
        localTeamName = this.localTeamName,
        visitorTeamName = this.visitorTeamName,
        localTeamImg = this.localTeamImg,
        visitorTeamImg = this.visitorTeamImg
    )
}
fun List<MatchEntity>.toLocal(): List<Match> = map { it.toExternal() }
