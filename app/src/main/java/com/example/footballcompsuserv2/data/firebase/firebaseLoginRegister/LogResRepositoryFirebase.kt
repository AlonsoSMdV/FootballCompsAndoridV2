package com.example.footballcompsuserv2.data.firebase.firebaseLoginRegister

import android.security.keystore.UserNotAuthenticatedException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LogResRepositoryFirebase @Inject constructor(
    private val auth: FirebaseAuth
) : ILogResRepositoryFirebase {

    override suspend fun login(identifier: String, password: String): Result<FirebaseUser> {
        return try {
            val res = auth.signInWithEmailAndPassword(identifier, password).await()
            res.user?.let { firebaseUser ->
                Result.success(
                    FirebaseUser(0, firebaseUser.uid, firebaseUser.email ?: "", "")
                )
            } ?: Result.failure(UserNotAuthenticatedException()) // Si el usuario es null
        } catch (ex: FirebaseAuthInvalidCredentialsException) {
            Result.failure(Exception("Credenciales incorrectas. Verifica tu correo y contraseña."))
        } catch (ex: FirebaseAuthInvalidUserException) {
            Result.failure(Exception("Usuario no encontrado."))
        } catch (ex: FirebaseAuthRecentLoginRequiredException) {
            Result.failure(Exception("Se requiere volver a iniciar sesión para completar esta acción."))
        } catch (ex: Exception) {
            Result.failure(Exception("Error de autenticación: ${ex.localizedMessage}"))
        }
    }

    override suspend fun register(user: String, email: String, password: String): Result<FirebaseUser> {
        return try {
            val res = auth.createUserWithEmailAndPassword(email, password).await()
            res.user?.let { firebaseUser ->
                Result.success(FirebaseUser(0, firebaseUser.uid, firebaseUser.email ?: "", user))
            } ?: Result.failure(UserNotAuthenticatedException()) // Si el usuario es null
        } catch (ex: FirebaseAuthUserCollisionException) {
            Result.failure(Exception("El correo ya está en uso."))
        } catch (ex: FirebaseAuthWeakPasswordException) {
            Result.failure(Exception("La contraseña es demasiado débil."))
        } catch (ex: Exception) {
            Result.failure(Exception("Error al registrar usuario: ${ex.localizedMessage}"))
        }
    }

    // FUNCIÓN: Logout con Firebase
    override suspend fun logout(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (ex: Exception) {
            Result.failure(Exception("Error al cerrar sesión: ${ex.localizedMessage}"))
        }
    }

    // FUNCIÓN: Obtener usuario autenticado actualmente
    override fun getActualUser(): FirebaseUser? {
        val currentUser = auth.currentUser
        return currentUser?.let {
            FirebaseUser(0, it.uid, it.email ?: "", "")
        }
    }
}