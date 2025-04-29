package com.example.footballcompsuserv2.data.firebase.firebaseUsers

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable


@IgnoreExtraProperties
data class UserFirebase(
    val id: String? = null,
    val name: String? = null,
    val surname: String? = null,
    val email: String? = null,
    val role: String? = null,
    val picture: String? = null,
    val playerFav: DocumentReference? = null,
    val teamFav: DocumentReference? = null,
    val leagueFav: DocumentReference? = null,
    val userId: String? = null
): Serializable