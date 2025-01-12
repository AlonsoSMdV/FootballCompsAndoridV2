package com.example.footballcompsuserv2.data.remote.leagues

data class CompRaw (
    val id: Int,
    val attributes: CompRawAttributes
)

data class CompRawAttributes(
    val name:String
)

data class CompCreate(val data: CompRawAttributes)