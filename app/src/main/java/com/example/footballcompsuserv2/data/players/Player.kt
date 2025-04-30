package com.example.footballcompsuserv2.data.players

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable

data class Player (
    val id: String,
    val name: String,
    val firstSurname: String,
    val secondSurname: String?,
    val birthdate: String,
    val nationality: String,
    val dorsal: Int,
    val position: String,
    val isFavourite: Boolean,
    val photo: String?,
    val teamId: String
)

@IgnoreExtraProperties
data class PlayerFb(
    var id: String? = null,
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