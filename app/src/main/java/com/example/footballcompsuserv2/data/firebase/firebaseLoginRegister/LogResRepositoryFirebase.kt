package com.example.footballcompsuserv2.data.firebase.firebaseLoginRegister

import android.security.keystore.UserNotAuthenticatedException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LogResRepositoryFirebase @Inject constructor(
    private val auth: FirebaseAuth
): ILogResRepositoryFirebase {
    override suspend fun login(identifier: String, password: String): Result<FirebaseUser> {
       try {
           val res = auth.signInWithEmailAndPassword(identifier, password).await()
           return Result.success(
               FirebaseUser(0, res.user!!.toString(), res.user!!.toString(), "")
           )
       }
       catch (ex: FirebaseAuthInvalidCredentialsException){
           return Result.failure(UserNotAuthenticatedException())
       }
    }

    override suspend fun register(
        user: String,
        email: String,
        password: String
    ): Result<FirebaseUser> {
        TODO("Not yet implemented")
    }

    override suspend fun logout() {
        auth.signOut()
    }
}