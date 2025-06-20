package com.example.footballcompsuserv2.data.user

import android.net.Uri
import android.util.Log
import com.example.footballcompsuserv2.data.leagues.CompetitionFb

import com.example.footballcompsuserv2.data.local.ILocalDataSource
import com.example.footballcompsuserv2.data.players.PlayerFb
import com.example.footballcompsuserv2.data.remote.user.UserRemoteDataSource
import com.example.footballcompsuserv2.data.teams.TeamFb
import com.example.footballcompsuserv2.di.Firestore
import com.example.footballcompsuserv2.di.NetworkUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await


import javax.inject.Inject

//Clase que obtiene los datos del usuario ya sea por remoto o local(si no hay red)
class UserRepository @Inject constructor(
    private val remoteDS: UserRemoteDataSource,
    private val local: ILocalDataSource,
    private val networkUtils: NetworkUtils
) : IUserRepository {

    private val _state = MutableStateFlow<List<User>>(listOf())
    override val setStream: StateFlow<List<User>>
        get() = _state.asStateFlow()

    private val _stateFb = MutableStateFlow<List<UserFb>>(listOf())
    override val setStreamFb: StateFlow<List<UserFb>>
        get() = _stateFb.asStateFlow()

    //Obtener los datos del usuario
    override suspend fun getActualUser(): User {
        var userId = 0;
        return if (networkUtils.isNetworkAvailable()) {//Si hay red los coge del remoto y los guarda en el local
            val res = remoteDS.getActualUser()
            if (res.isSuccessful) {
                val user = res.body()?.toExternal() ?: User("", "", "")
                Log.d("UserRepository", "Usuario obtenido de API: $user")
                local.createLocalUser(user.toLocal()) // Guarda en local
                userId = user.id.toInt()
                user
            } else {
                Log.e("UserRepository", "Error en la API: ${res.errorBody()?.string()}")
                User("", "", "")
            }
        } else {// sino los carga en el local(Hay que arreglar esta parte)
            try {
                val localUser = local.getLocalUser(userId)
                Log.d("UserRepository", "Usuario obtenido de LOCAL: $localUser")
                localUser?.localToExternal() ?: User("", "", "")
            } catch (e: Exception) {
                Log.e("UserRepository", "Error al obtener usuario local", e)
                User("", "", "")
            }
        }
    }

    override suspend fun getActualUserFb(): UserFb {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val firestore = FirebaseFirestore.getInstance()

        val querySnapshot = firestore
            .collection("usuarios")
            .whereEqualTo("userId", uid)
            .get()
            .await()

        if (!querySnapshot.isEmpty) {
            val documentSnapshot = querySnapshot.documents.first()
            val user = documentSnapshot.toObject(UserFb::class.java)
            Log.d("UserRepository", "Usuario obtenido correctamente: $user")
            return user ?: UserFb()
        } else {
            Log.e("UserRepository", "No se encontró ningún documento con userId = $uid")
            return UserFb()
        }

    }

    override suspend fun getActualUserFbId(): String {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val firestore = FirebaseFirestore.getInstance()

        val querySnapshot = firestore
            .collection("usuarios")
            .whereEqualTo("userId", uid)
            .get()
            .await()

        if (!querySnapshot.isEmpty) {
            val documentSnapshot = querySnapshot.documents.first()
            val userId = documentSnapshot.id
            Log.d("UserRepository", "Id de Usuario obtenido correctamente: $userId")
            return userId ?: ""
        } else {
            Log.e("UserRepository", "No se encontró ningún documento con userId = $uid")
            return ""
        }

    }





    //Firebase
    private val firestore = Firestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    override suspend fun updateUser(userId: String, userFb: UserFb): Boolean {
        return try {
            firestore.collection("usuarios")
                .document(userId)
                .set(userFb)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun uploadImageToFirebaseStorage(uri: Uri): String? {
        return try {
            val storage = FirebaseStorage.getInstance()
            val fileName = "uploads/${System.currentTimeMillis()}.jpg"
            val imageRef = storage.reference.child(fileName)

            imageRef.putFile(uri).await()
            imageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun updateUserWithOptionalImage(
        userId: String,
        updatedData: UserFb,
        imageUri: Uri?
    ): Boolean {
        return try {
            val finalTeam = if (imageUri != null) {
                val imageUrl = uploadImageToFirebaseStorage(imageUri)
                updatedData.copy(picture = imageUrl ?: "")
            } else updatedData

            updateUser(userId, finalTeam)
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getFavoriteTeam(reference: DocumentReference?): TeamFb? {
        return try {
            reference?.get()?.await()?.toObject(TeamFb::class.java)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error al obtener equipo favorito", e)
            null
        }
    }

    override suspend fun getFavoritePlayer(reference: DocumentReference?): PlayerFb? {
        return try {
            reference?.get()?.await()?.toObject(PlayerFb::class.java)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error al obtener jugador favorito", e)
            null
        }
    }

    override suspend fun getFavoriteLeague(reference: DocumentReference?): CompetitionFb? {
        return try {
            reference?.get()?.await()?.toObject(CompetitionFb::class.java)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error al obtener liga favorita", e)
            null
        }
    }
    override suspend fun updateUserLeagueFav(leagueRef: DocumentReference) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val firestore = FirebaseFirestore.getInstance()

        val querySnapshot = firestore
            .collection("usuarios")
            .whereEqualTo("userId", uid)
            .get()
            .await()

        if (!querySnapshot.isEmpty) {
            val documentSnapshot = querySnapshot.documents.first()
            val userDocRef = documentSnapshot.reference

            userDocRef.update("leagueFav", leagueRef)
                .addOnSuccessListener {
                    Log.d("UserRepository", "Liga favorita actualizada correctamente")
                }
                .addOnFailureListener {
                    Log.e("UserRepository", "Error al actualizar liga favorita", it)
                }
        } else {
            Log.e("UserRepository", "Usuario no encontrado para actualizar liga favorita")
        }
    }

    override suspend fun updateUserTeamFav(teamRef: DocumentReference) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val firestore = FirebaseFirestore.getInstance()

        val querySnapshot = firestore
            .collection("usuarios")
            .whereEqualTo("userId", uid)
            .get()
            .await()

        if (!querySnapshot.isEmpty) {
            val documentSnapshot = querySnapshot.documents.first()
            val userDocRef = documentSnapshot.reference

            userDocRef.update("teamFav", teamRef)
                .addOnSuccessListener {
                    Log.d("UserRepository", "Equipo favorito actualizada correctamente")
                }
                .addOnFailureListener {
                    Log.e("UserRepository", "Error al actualizar equipo favorito", it)
                }
        } else {
            Log.e("UserRepository", "Usuario no encontrado para actualizar equipo favorito")
        }
    }

    override suspend fun updateUserPlayerFav(playerRef: DocumentReference) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val firestore = FirebaseFirestore.getInstance()

        val querySnapshot = firestore
            .collection("usuarios")
            .whereEqualTo("userId", uid)
            .get()
            .await()

        if (!querySnapshot.isEmpty) {
            val documentSnapshot = querySnapshot.documents.first()
            val userDocRef = documentSnapshot.reference

            userDocRef.update("playerFav", playerRef)
                .addOnSuccessListener {
                    Log.d("UserRepository", "Jugador favorito actualizada correctamente")
                }
                .addOnFailureListener {
                    Log.e("UserRepository", "Error al actualizar jugador favorito", it)
                }
        } else {
            Log.e("UserRepository", "Usuario no encontrado para actualizar jugador favorito")
        }
    }

}