package com.example.skillcinema.domain.model

data class StaffInfo(
    val personId: Long,
    val name: String,
    val sex: String,
    val posterUrl: String,
    val profession: String,
    var films: List<Film>
)