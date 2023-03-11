package com.example.skillcinema.domain.model

data class LocalCollection(
    val id: Int,
    val name: String,
    val isDefault: Boolean,
    val filmsIds: List<Long>?
)
