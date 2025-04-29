package com.example.footballcompsuserv2.data.firebase.firebaseTeams

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable


@IgnoreExtraProperties
data class TeamFirebase(
    val id: String? = null,
    val name: String? = null,
    val numberOfPlayers: String? = null,
    val nMatches: Int? = null,
    val pts: Int? = null,
    val picture: String? = null,
    val league: DocumentReference? = null,
    val userId: DocumentReference? = null
): Serializable