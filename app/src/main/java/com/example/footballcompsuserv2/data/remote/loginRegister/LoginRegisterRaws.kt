package com.example.footballcompsuserv2.data.remote.loginRegister

data class RegisterRaw(
    val username: String,
    val email: String,
    val password: String
)
data class LoginRaw(
    val identifier: String,
    val password: String
)
data class LoginRegisterResponse(
    val jwt: String,
    val user: LoggedUser
)
data class LoggedUser(
    val id: Int
)