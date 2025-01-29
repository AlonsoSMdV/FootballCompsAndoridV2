package com.example.footballcompsuserv2.data.user

import com.example.footballcompsuserv2.data.remote.user.UserRaw

fun UserRaw.toExternal(): User{
    return User(
        id = this.id.toString(),
        name = this.name,
        email = this.email
    )
}

fun List<UserRaw>.toExternal():List<User> = map(UserRaw::toExternal)