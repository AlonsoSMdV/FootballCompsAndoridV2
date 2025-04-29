package com.example.footballcompsuserv2.di


import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

object Firestore {
    private var instance: FirebaseFirestore? = null

    fun getInstance(): FirebaseFirestore {
        if (instance == null) {
            instance = FirebaseFirestore.getInstance().apply {
                firestoreSettings = FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .build()
            }
        }
        return instance!!
    }
}