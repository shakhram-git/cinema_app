package com.example.skillcinema.data.local.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "collections_films_relation",
    primaryKeys = ["collection_id", "film_id"],
    indices = [Index("film_id")],
    foreignKeys = [
        ForeignKey(
            entity = CollectionDbo::class,
            parentColumns = ["id"],
            childColumns = ["collection_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )/*,
        ForeignKey(
            entity = FilmDbo::class,
            parentColumns = ["id"],
            childColumns = ["film_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )*/
    ]
)
data class CollectionFilmDbo(
    @ColumnInfo(name = "collection_id")
    val collectionId: Int,
    @ColumnInfo(name = "film_id")
    val filmId: Long,
    @ColumnInfo("created_at")
    val createdAt: Long
)