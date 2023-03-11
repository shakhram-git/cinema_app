package com.example.skillcinema.data.local.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watched_films_table")
data class WatchedFilm(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Long
)