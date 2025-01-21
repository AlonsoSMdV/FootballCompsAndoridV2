package com.example.footballcompsuserv2.data.leagues

data class Competition (
    val id: String,
    val name: String,
    val logo: String?,
    val logoId: String?,
    val logoName:String?,
    val isFavourite: Boolean
)