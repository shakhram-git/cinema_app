package com.example.skillcinema.data.remote.retrofit.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StaffInfoDto(
    val personId: Long,
    val nameRu: String,
    val nameEn: String,
    val sex: String,
    val posterUrl: String,
    val profession: String,
    val films: List<FilmDto>?
)