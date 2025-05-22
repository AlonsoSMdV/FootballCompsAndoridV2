package com.example.footballcompsuserv2.data.matchStatistics

import android.util.Log
import com.example.footballcompsuserv2.data.user.UserFb
import com.example.footballcompsuserv2.di.Firestore
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StatsRepository @Inject constructor(): IStatsRepository{
    private val _stateFb = MutableStateFlow<List<StatsFb>>(listOf())
    override val setStreamFb: StateFlow<List<StatsFb>>
        get() = _stateFb.asStateFlow()

    override suspend fun getStatsByMatch(idMatch: String): StatsFb {
        val firestore = Firestore.getInstance()
        val collection = firestore.collection("matchStatistics")

        val matchRef = Firestore.getInstance().collection("matches").document(idMatch)

        val snapshot = collection
            .whereEqualTo("matchId", matchRef)
            .get()
            .await()

        if (!snapshot.isEmpty) {
            val documentSnapshot = snapshot.documents.first()
            val stats = documentSnapshot.toObject(StatsFb::class.java)
            return stats ?: StatsFb()
        } else {
            return StatsFb()
        }
    }
    override suspend fun addStatFb(statsFb: StatsFbFields, idMatch: String): Boolean {
        return try {
            val matchRef = Firestore.getInstance().collection("matches").document(idMatch)

            val matchToSave = statsFb.copy(matchId = matchRef)

            Firestore.getInstance().collection("matchStatistics")
                .add(matchToSave)
                .await()

            true
        } catch (e: Exception) {
            false
        }
    }
}