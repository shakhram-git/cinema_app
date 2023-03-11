package com.example.skillcinema.domain.model

data class Staff(
    val staffId: Long,
    val name: String,
    val description: String?,
    val posterUrl: String,
    val professionText: String,
    val professionKey: String
)