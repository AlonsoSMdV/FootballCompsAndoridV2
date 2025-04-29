package com.example.footballcompsuserv2.data.leagues

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable

data class Competition (
    val id: String? = null,
    val name: String? = null,
    val logo: String? = null,
    val isFavourite: Boolean,
    val userId: String? = null,
){
    // Constructor vacío necesario para Firestore
    constructor() : this("", "", "", false,"")
}

@IgnoreExtraProperties
data class CompetitionFb (
    val id: String? = null,
    val name: String? = null,
    val picture: String? = null,
    val userId: DocumentReference? = null,
):Serializable{
    // Constructor vacío necesario para Firestore
    constructor() : this("", "", "",null)
}