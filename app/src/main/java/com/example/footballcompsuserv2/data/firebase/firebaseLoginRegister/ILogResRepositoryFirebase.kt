package com.example.footballcompsuserv2.data.firebase.firebaseLoginRegister


interface ILogResRepositoryFirebase {
    suspend fun login(identifier: String, password: String): Result<FirebaseUser>
    suspend fun register(user: String, email: String, password: String): Result<FirebaseUser>
    suspend fun logout()
}