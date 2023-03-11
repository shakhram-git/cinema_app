package com.example.skillcinema.data.local.room

import androidx.room.*
import com.example.skillcinema.DefaultCollections
import com.example.skillcinema.data.local.room.model.CollectionDbo
import com.example.skillcinema.data.local.room.model.CollectionWithFilmsDbo
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {

    @Transaction
    @Query("SELECT * FROM collections WHERE id = :collectionId")
    fun getCollectionWithFilmsFlow(collectionId: Int): Flow<CollectionWithFilmsDbo>

    @Transaction
    @Query("SELECT * FROM collections WHERE id = :collectionId")
    fun getCollectionWithFilms(collectionId: Int): CollectionWithFilmsDbo

    @Transaction
    @Query("SELECT * FROM collections WHERE id NOT IN (${DefaultCollections.INTERESTED}, ${DefaultCollections.WATCHED})")
    fun getAllCollectionsInfo(): Flow<List<CollectionWithFilmsDbo>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun createNewCollection(collectionDbo: CollectionDbo): Long

    @Query("SELECT film_id FROM collections_films_relation WHERE collection_id = :collectionId")
    fun getCollectionFilmsIds(collectionId: Int): Flow<List<Long>>

    @Query("DELETE FROM collections_films_relation WHERE collection_id = :collectionId")
    suspend fun cleanCollection(collectionId: Int)

    @Transaction
    @Query("DELETE FROM collections_films_relation WHERE film_id IN (" +
            "SELECT film_id FROM collections_films_relation " +
            "WHERE collection_id = ${DefaultCollections.INTERESTED} " +
            "ORDER BY created_at DESC LIMIT -1 OFFSET 80)")
    suspend fun limitInterestedCollection()

    @Transaction
    @Query("DELETE FROM collections WHERE id = :collectionId")
    suspend fun deleteCollection(collectionId: Int)

}