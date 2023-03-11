package com.example.skillcinema.data.remote.retrofit.model

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class FilmPhotoDto(
    val imageUrl: String,
    val previewUrl: String
)