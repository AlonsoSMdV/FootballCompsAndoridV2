package com.example.footballcompsuserv2.data.user

import com.example.footballcompsuserv2.data.firebase.firebaseUsers.UserFirebase
import com.example.footballcompsuserv2.data.leagues.fbToExternal
import com.example.footballcompsuserv2.data.local.entities.UserEntity
import com.example.footballcompsuserv2.data.remote.user.UserRaw
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

//MAPPEOS DE CLASES
//REMOTO->NEUTRAL
fun UserRaw.toExternal(): User{
    return User(
        id = this.id.toString(),
        name = this.username ?: "Nombre no disponible",
        email = this.email ?: "Correo no disponible"
    )
}

fun List<UserRaw>.toExternal():List<User> = map(UserRaw::toExternal)

//NEUTRAL -> LOCAL
fun User.toLocal(): UserEntity{
    return UserEntity(
        id = this.id.toInt(),
        userName = this.name ?: "Nombre no disponible",
        email = this.email ?: "Correo no disponible",
        token = null
    )
}

fun List<User>.toLocal():List<UserEntity> = map { it.toLocal() }

//LOCAL -> NEUTRAL
fun UserEntity.localToExternal(): User{
    return User(
        id = this.id.toString(),
        name = this.userName ?: "Nombre no disponible",
        email = this.email ?: "Correo no disponible"
    )
}

fun List<UserEntity>.localToExternal():List<User> = map { it.localToExternal() }

fun UserFb.fbToExternal(firestore: FirebaseFirestore): UserFirebase{
    val playerRef: DocumentReference = firestore.collection("players").document(this.playerFav!!.id)
    val teamRef: DocumentReference = firestore.collection("teams").document(this.teamFav!!.id)
    val leagueRef: DocumentReference = firestore.collection("leagues").document(this.leagueFav!!.id)
    return UserFirebase(
        id = this.id!!,
        name = this.name!!,
        surname = this.surname!!,
        email = this.email!!,
        role = this.role!!,
        picture = this.picture!!,
        playerFav = playerRef,
        teamFav = teamRef,
        leagueFav = leagueRef,
        userId = this.userId!!
    )
}
fun List<UserFb>.fbToExternal(firestore: FirebaseFirestore): List<UserFirebase> = map { it.fbToExternal(firestore) }

fun UserFirebase.fbToLocal(): UserFb{
    return UserFb(
        id = this.id!!,
        name = this.name!!,
        surname = this.surname!!,
        email = this.email!!,
        role = this.role!!,
        picture = this.picture!!,
        playerFav = this.playerFav!!,
        teamFav = this.teamFav!!,
        leagueFav = this.leagueFav!!,
        userId = this.userId!!
    )
}
fun List<UserFirebase>.fbToLocal(): List<UserFb> = map { it.fbToLocal() }