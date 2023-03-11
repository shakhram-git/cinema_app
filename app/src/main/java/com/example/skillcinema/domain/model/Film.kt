package com.example.skillcinema.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Film(
    val kinopoiskId: Long,
    val name: String,
    var posterUrlPreview: String?,
    val genre: String?,
    val premiereRu: String?,
    val rating: String?,
    var isWatched: Boolean,
    val professionKey: String?,
    val year: Int?,
    var savedAt: Long? = null
) : Parcelable
