package com.example.skillcinema.data.remote.retrofit.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FilmPhotosListDto(
    val total: Int,
    val items: List<FilmPhotoDto>
)