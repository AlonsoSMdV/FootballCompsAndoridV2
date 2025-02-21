package com.example.footballcompsuserv2.data.players

import com.example.footballcompsuserv2.data.local.entities.PlayerEntity
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
        teamId = this.attributes.team?.data?.id.toString(),
        isFavourite = this.attributes.isFavourite,
        photo = this.attributes.playerProfilePhoto?.data?.attributes?.formats?.small?.url ?:"",
        birthdate = this.attributes.birthdate
    )
}
fun List<PlayerRaw>.toExternal():List<Player> = map(PlayerRaw::toExternal)

fun Player.toLocal(): PlayerEntity{
    return PlayerEntity(
        id = this.id.toInt(),
        name = this.name,
        firstSurname = this.firstSurname,
        secondSurname = this.secondSurname,
        nationality = this.nationality,
        dorsal = this.dorsal,
        position = this.position,
        teamId = this.teamId,
        isFavourite = this.isFavourite,
        photo = this.photo,
        birthdate = this.birthdate
    )
}
fun List<Player>.toLocal():List<PlayerEntity> = map{it.toLocal()}

fun PlayerEntity.localToExternal(): Player{
    return Player(
        id = this.id.toString(),
        name = this.name,
        firstSurname = this.firstSurname,
        secondSurname = this.secondSurname,
        nationality = this.nationality,
        dorsal = this.dorsal,
        position = this.position,
        teamId = this.teamId,
        isFavourite = this.isFavourite,
        photo = this.photo,
        birthdate = this.birthdate
    )
}
fun List<PlayerEntity>.localToExternal():List<Player> = map{it.localToExternal()}