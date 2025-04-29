package com.example.footballcompsuserv2.data.firebase.firebasePlayers

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable


@IgnoreExtraProperties
data class PlayerFirebase(
    val id: String? = null,
    val name: String? = null,
    val firstSurname: String? = null,
    val secondSurname: String? = null,
    val position: String? = null,
    val dorsal: String? = null,
    val birthdate: String? = null,
    val nationality: String? = null,
    val picture: String? = null,
    val team: DocumentReference? = null,
    val userId: DocumentReference? = null
): Serializable