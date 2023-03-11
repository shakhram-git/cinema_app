package com.example.skillcinema.domain.model

data class Seasons(
    val total: Int,
    val seasons: List<Season>
) {
    data class Season (
        val number: Int,
        val episodes: List<Episode>
    )

}
