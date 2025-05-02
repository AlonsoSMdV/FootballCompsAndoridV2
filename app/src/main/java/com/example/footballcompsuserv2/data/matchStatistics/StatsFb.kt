package com.example.footballcompsuserv2.data.matchStatistics

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable

data class Stat(
    val localValue: Int? = null,
    val name: String? = null,
    val visitorValue: Int? = null
)


@IgnoreExtraProperties
data class StatsFb(
    var id: String? = null,
    val matchId: DocumentReference? = null,
    val stats: List<Stat>? = null
): Serializable

@IgnoreExtraProperties
data class StatsFbFields(
    val matchId: DocumentReference? = null,
    val stats: List<Stat>? = null
): Serializable