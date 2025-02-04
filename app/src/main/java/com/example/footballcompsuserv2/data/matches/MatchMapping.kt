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
        visitor = this.attributes.visitor.toString()
    )
}

fun List<MatchesRaw>.toExternal(): List<Match> = map ( MatchesRaw::toExternal )
