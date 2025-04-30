package com.example.footballcompsuserv2.data.user

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

