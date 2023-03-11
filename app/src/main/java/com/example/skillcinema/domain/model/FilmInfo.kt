package com.example.skillcinema.domain.model

data class FilmInfo(
    val kinopoiskId: Long,

    val nameRu: String?,
    val nameOriginal: String?,


    val posterUrlPreview: String,
    val posterUrl: String,
    val coverUrl: String?,
    val logoUrl: String?,
    val rating: Double?,
    val webUrl: String,
    val year: Int?,
    val filmLength: Long?,

    val countries: List<String>,
    val genres: List<String>,

    val ratingAgeLimits: String?,

    val description: String?,
    val shortDescription: String?,

    val isSerial: Boolean,
    val isCompleted: Boolean,

    var seriesInfo: SeriesInfo? = null

)
