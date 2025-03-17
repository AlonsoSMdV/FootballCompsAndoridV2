package com.example.footballcompsuserv2.data.firebase.firebaseLeagues

import com.example.footballcompsuserv2.data.leagues.Competition
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LeagueFirebase @Inject constructor(): ILeagueFirebase{
    private val firestore = FirebaseFirestore.getInstance()
    private val leaguesCollection = firestore.collection("leagues")

    override suspend fun getAllLeagues(): List<Competition> {
        val leagues = leaguesCollection
            .get()
            .await()

        return leagues.documents.mapNotNull { doc ->
            doc.toObject(Competition::class.java)
        }
    }

    override suspend fun createLeague(league: Competition) {
        leaguesCollection.document().set(league)
    }

    override suspend fun deleteComp() {
        leaguesCollection.document().delete()
    }
}