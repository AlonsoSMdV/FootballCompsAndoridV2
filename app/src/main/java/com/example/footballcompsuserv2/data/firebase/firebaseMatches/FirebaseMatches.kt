package com.example.footballcompsuserv2.data.firebase.firebaseMatches

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable


@IgnoreExtraProperties
data class MatchFirebase(
    val id: String? = null,
    val day: String? = null,
    val hour: String? = null,
    val place: String? = null,
    val result: String? = null,
    val status: String? = null,
    val picture: String? = null,
    val localTeamId: DocumentReference? = null,
    val visitorTeamId: DocumentReference? = null
): Serializable