package com.example.skillcinema.data.local.room

import androidx.room.*
import com.example.skillcinema.data.local.room.model.CollectionFilmDbo
import com.example.skillcinema.data.local.room.model.FilmDbo

@Dao
interface FilmDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(filmDbo: FilmDbo)

    @Insert(entity = CollectionFilmDbo::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFilmToCollection(relation: CollectionFilmDbo)

    @Query("DELETE FROM collections_films_relation WHERE collection_id = :collectionId AND film_id = :filmId")
    suspend fun removeFilmFromCollection(collectionId: Int, filmId: Long)

    @Transaction
    @Query("DELETE FROM films WHERE id IN (" +
            "SELECT films.id FROM films LEFT JOIN collections_films_relation " +
            "ON films.id = collections_films_relation.film_id WHERE collections_films_relation.collection_id IS NULL )")
    suspend fun refreshFilms()


}