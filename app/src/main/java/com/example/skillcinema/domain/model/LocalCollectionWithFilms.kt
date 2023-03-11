package com.example.skillcinema.domain.model

data class LocalCollectionWithFilms(
    val id: Int,
    val name: String,
    val isDefault: Boolean,
    var films: List<Film>
)