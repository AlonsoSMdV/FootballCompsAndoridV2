package com.example.footballcompsuserv2.data.teams

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import java.io.Serializable

data class Team (
    val id: String,
    val name: String,
    val isFavourite: Boolean,
    val nPlayers: Int,
    val tLogo: String?,
    val comId: String
)

@IgnoreExtraProperties
data class TeamFb(
    var id: String? = null,
    val name: String? = null,
    val numberOfPlayers: String? = null,
    @get:PropertyName("nMatches") @set:PropertyName("nMatches")
    var nMatches: Int? = null,
    val pts: Int? = null,
    val picture: String? = null,
    val league: DocumentReference? = null,
    val userId: DocumentReference? = null
): Serializable

@IgnoreExtraProperties
data class TeamFbFields(
    val name: String? = null,
    val numberOfPlayers: String? = null,
    @get:PropertyName("nMatches") @set:PropertyName("nMatches")
    var nMatches: Int? = null,
    val pts: Int? = null,
    val picture: String? = null,
    val league: DocumentReference? = null,
    val userId: DocumentReference? = null
): Serializable