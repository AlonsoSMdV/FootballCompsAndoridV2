package com.example.footballcompsuserv2.data.matches

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
