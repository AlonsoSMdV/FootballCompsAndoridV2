package com.example.footballcompsuserv2.data.leagues

data class Competition (
    val id: String,
    val name: String,
    val logo: String?,
    val isFavourite: Boolean
){
    // Constructor vacío necesario para Firestore
    constructor() : this("", "", "", false)
}