package com.example.footballcompsuserv2.data.user

import com.example.footballcompsuserv2.data.remote.user.UserRaw

fun UserRaw.toExternal(): User{
    return User(
        id = this.id.toString(),
        name = this.username ?: "Nombre no disponible",
        email = this.email ?: "Correo no disponible"
    )
}

fun List<UserRaw>.toExternal():List<User> = map(UserRaw::toExternal)