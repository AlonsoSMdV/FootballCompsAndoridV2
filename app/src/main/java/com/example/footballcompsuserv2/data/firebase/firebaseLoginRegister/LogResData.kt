package com.example.footballcompsuserv2.data.firebase.firebaseLoginRegister

data class FirebaseLoginData(
    val identifier: String,
    val password: String
)
data class FirebaseRegisterData(
    val user: String,
    val email: String,
    val password: String
)

data class FirebaseUser(
    val id: Int,
    val user: String,
    val email: String,
    val token: String?
)
