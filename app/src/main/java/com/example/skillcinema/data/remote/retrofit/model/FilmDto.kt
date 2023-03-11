package com.example.skillcinema.data.remote.retrofit.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FilmDto (
    val kinopoiskId: Long?,
    val filmId: Long?,
    val nameRu: String?,
    val nameEn: String?,
    val nameOriginal: String?,
    val posterUrlPreview: String?,
    val genres: List<GenreDto>?,
    val premiereRu: String?,
    val rating: String?,
    val ratingKinopoisk: Double?,
    val professionKey: String?,
    val year: Int?
)

@JsonClass(generateAdapter = true)
data class GenreDto (
    val genre: String
)

@JsonClass(generateAdapter = true)
data class CountryDto (
    val country: String
)




