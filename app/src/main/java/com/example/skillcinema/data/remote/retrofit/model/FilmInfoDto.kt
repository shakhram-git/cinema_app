package com.example.skillcinema.data.remote.retrofit.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FilmInfoDto (
    val kinopoiskId: Long,

    val nameRu: String?,
    val nameOriginal: String?,
    val posterUrl: String,
    val coverUrl: String?,
    val logoUrl: String?,
    val posterUrlPreview: String,

    val ratingKinopoisk: Double?,
    val ratingImdb: Double?,
    val webUrl: String,
    val year: Int?,
    val filmLength: Long?,
    val description: String?,
    val shortDescription: String?,
    val ratingAgeLimits: String?,
    val lastSync: String,
    val countries: List<CountryDto>,
    val genres: List<GenreDto>,
    val serial: Boolean,
    val completed: Boolean
)



