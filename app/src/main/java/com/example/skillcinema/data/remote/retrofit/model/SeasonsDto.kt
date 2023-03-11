package com.example.skillcinema.data.remote.retrofit.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SeasonsDto(
    val total: Int,
    val items: List<SeasonDto>
) {
    @JsonClass(generateAdapter = true)
    data class SeasonDto (
        val number: Int,
        val episodes: List<EpisodeDto>
    )
    @JsonClass(generateAdapter = true)
    data class EpisodeDto (
        val seasonNumber: Int,
        val episodeNumber: Int,
        val nameRu: String?,
        val nameEn: String?,
        val synopsis: String?,
        val releaseDate: String?
    )
}

