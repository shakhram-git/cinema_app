package com.example.skillcinema.domain.model

data class SearchConfig(
    val country: Country?,
    val genre: Genre?,
    val order: String,
    val type: String,
    val ratingFrom: Int,
    val ratingTo: Int,
    val yearFrom: Int,
    val yearTo: Int,
    val keyword: String?,
    val isShowingWatched: Boolean
)
