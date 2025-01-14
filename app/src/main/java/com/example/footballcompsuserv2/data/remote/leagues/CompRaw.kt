package com.example.footballcompsuserv2.data.remote.leagues

data class CompRaw (
    val id: Int,
    val attributes: CompRawAttributes
)

data class CompRawAttributes(
    val name:String,
    val isFavourite: Boolean,
    val logo: LogoWrapper?
)
data class LogoRaw(
    val id: Int,
    val attributes: LogoRawAttributes
)

data class LogoWrapper(
    val data: LogoRaw?
)

data class LogoRawAttributes(
    val name: String,
    val formats: FormatLogo?
)

data class FormatLogo(
    val small: LogoDetail
)

data class LogoDetail(
    val url: String
)

data class CompCreate(val data: CompRawAttributes)