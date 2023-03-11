package com.example.skillcinema.data.local.room.model

import androidx.room.Embedded
import androidx.room.Relation

/*data class CollectionWithFilmsDbo(
    @Embedded
    val collection: CollectionDbo,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            CollectionFilmDbo::class,
            parentColumn = "collection_id",
            entityColumn = "film_id"
        )
    )
    val films: List<FilmDbo>
)*/


data class CollectionWithFilmsDbo(
    @Embedded
    val collection: CollectionDbo,
    @Relation(
        parentColumn = "id",
        entityColumn = "collection_id",
        entity = CollectionFilmDbo::class
    )
    val films: List<FilmWithRelation>
)

data class FilmWithRelation(
    @Embedded
    val relation: CollectionFilmDbo,
    @Relation(
        parentColumn = "film_id",
        entityColumn = "id"
    )
    val film: FilmDbo
)