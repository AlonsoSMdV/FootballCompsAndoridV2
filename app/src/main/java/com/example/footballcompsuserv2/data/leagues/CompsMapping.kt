package com.example.footballcompsuserv2.data.leagues


import com.example.footballcompsuserv2.data.remote.leagues.CompRaw

fun CompRaw.toExternal(): Competition {
    return Competition(
        id = this.id.toString(),
        name = this.attributes.name,
        logo = this.attributes.logo?.data?.attributes?.formats?.small?.url ?:"",
        isFavourite = this.attributes.isFavourite
    )
}
fun List<CompRaw>.toExternal():List<Competition> = map(CompRaw::toExternal)