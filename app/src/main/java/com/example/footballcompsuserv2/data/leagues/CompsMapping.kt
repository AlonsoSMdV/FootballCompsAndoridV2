package com.example.footballcompsuserv2.data.leagues


import com.example.footballcompsuserv2.data.local.entities.LeagueEntity
import com.example.footballcompsuserv2.data.remote.leagues.CompRaw
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

//MAPPEOS DE CLASES
//REMOTO->NEUTRAL
fun CompRaw.toExternal(): Competition {
    return Competition(
        id = this.id.toString(),
        name = this.attributes.name,
        logo = this.attributes.logo?.data?.attributes?.formats?.small?.url ?: "",
        isFavourite = this.attributes.isFavourite
    )
}
fun List<CompRaw>.toExternal():List<Competition> = map(CompRaw::toExternal)

//NEUTRAL -> LOCAL
fun Competition.toLocal(): LeagueEntity{
    return LeagueEntity(
        id = this.id!!.toInt(),
        name = this.name!!,
        isFavourite = this.isFavourite,
        logo = this.logo
    )
}
fun List<Competition>.toLocal(): List<LeagueEntity> = map { it.toLocal() }

//LOCAL -> NEUTRAL
fun LeagueEntity.localToExternal(): Competition{
    return Competition(
        id = this.id.toString(),
        name = this.name,
        isFavourite = this.isFavourite,
        logo = this.logo
    )
}
fun List<LeagueEntity>.localToExternal(): List<Competition> = map { it.localToExternal() }

