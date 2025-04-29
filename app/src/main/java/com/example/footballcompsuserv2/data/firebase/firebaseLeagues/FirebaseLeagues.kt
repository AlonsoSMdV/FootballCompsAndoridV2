package com.example.footballcompsuserv2.data.firebase.firebaseLeagues

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class League(
    val id: String? = null,
    val name: String? = null,
    val picture: String? = null,
    val userId: DocumentReference? = null
): Serializable