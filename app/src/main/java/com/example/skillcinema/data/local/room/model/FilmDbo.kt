package com.example.skillcinema.data.local.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "films")
data class FilmDbo(
    @PrimaryKey
    val id: Long,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "poster_url_preview")
    val posterUrlPreview: String,
    @ColumnInfo(collate = ColumnInfo.NOCASE)
    val genre: String?,
    val rating: String?
)
