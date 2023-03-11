package com.example.skillcinema.data.remote.retrofit.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StaffDto(
    val staffId: Long,
    val nameRu: String,
    val nameEn: String,
    val description: String?,
    val posterUrl: String,
    val professionText: String,
    val professionKey: String
)