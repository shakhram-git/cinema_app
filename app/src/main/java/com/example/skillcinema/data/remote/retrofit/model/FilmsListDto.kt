package com.example.skillcinema.data.remote.retrofit.model

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class FilmsListDto(
    val items: List<FilmDto>?,
    val films: List<FilmDto>?
)