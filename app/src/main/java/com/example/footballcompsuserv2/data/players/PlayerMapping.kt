package com.example.footballcompsuserv2.data.players

import com.example.footballcompsuserv2.data.players.Player
import com.example.footballcompsuserv2.data.remote.players.PlayerRaw
import com.example.footballcompsuserv2.data.remote.players.PlayerRawAttributes

fun PlayerRaw.toExternal(): Player {
    return Player(
        id = this.id.toString(),
        name = this.attributes.name,
        firstSurname = this.attributes.firstSurname,
        secondSurname = this.attributes.secondSurname,
        nationality = this.attributes.nationality,
        dorsal = this.attributes.dorsal,
        position = this.attributes.position,
        teamId = this.attributes.team.toString(),
        isFavourite = this.attributes.isFavourite,
        photo = this.attributes.playerProfilePhoto?.data?.attributes?.formats?.small?.url ?:"",
        birthdate = this.attributes.birthdate
    )
}
fun List<PlayerRaw>.toExternal():List<Player> = map(PlayerRaw::toExternal)