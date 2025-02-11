package com.example.footballcompsuserv2.data.remote.uploadImg

data class Media(
    val documentId: String,
    val formats: MediaFormats
)
data class MediaFormats(
    val small: ImageAttributes,
    val thumbnail: ImageAttributes,
)
data class ImageAttributes(
    val url: String
)
data class CreatedMediaItemResponse(
    val id:Int,
    val documentId: String,
    val name:String,
    val formats: MediaFormats
)
data class StrapiResponse<T>(
    val data: T
)