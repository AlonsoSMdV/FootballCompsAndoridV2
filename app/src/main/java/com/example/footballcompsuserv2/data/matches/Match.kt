package com.example.footballcompsuserv2.data.matches

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable

data class Match (
    val id: String,
    val day: String,
    val hour: String,
    val result: String?,
    val place : String,
    val local: String?,
    val visitor: String?,
    val localTeamName: String?,
    val visitorTeamName: String?,
    val localTeamImg: String?,
    val visitorTeamImg: String?
)

@IgnoreExtraProperties
data class MatchFb(
    var id: String? = null,
    val day: String? = null,
    val hour: String? = null,
    val place: String? = null,
    val result: String? = null,
    val status: String? = null,
    val picture: String? = null,
    val localTeamId: DocumentReference? = null,
    val visitorTeamId: DocumentReference? = null
): Serializable

@IgnoreExtraProperties
data class MatchFbFields(
    val day: String? = null,
    val hour: String? = null,
    val place: String? = null,
    val result: String? = null,
    val status: String? = null,
    val picture: String? = null,
    val localTeamId: DocumentReference? = null,
    val visitorTeamId: DocumentReference? = null
): Serializable

@IgnoreExtraProperties
data class MatchFbWithTeams(
    var id: String? = null,
    val day: String? = null,
    val hour: String? = null,
    val place: String? = null,
    val result: String? = null,
    val status: String? = null,
    val localTeamId: DocumentReference? = null,
    val visitorTeamId: DocumentReference? = null,
    val localTeamName: String?,
    val visitorTeamName: String?,
    val localTeamImg: String?,
    val visitorTeamImg: String?
): Serializable