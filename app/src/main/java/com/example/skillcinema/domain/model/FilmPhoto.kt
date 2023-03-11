package com.example.skillcinema.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FilmPhoto(
    val imageUrl: String,
    val previewUrl: String
): Parcelable
