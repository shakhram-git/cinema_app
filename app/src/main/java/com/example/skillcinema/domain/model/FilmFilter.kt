package com.example.skillcinema.domain.model

data class FilmFilter(
    val genreId: Long,
    val genreName: String,
    val countryId: Long,
    val countryName: String
)