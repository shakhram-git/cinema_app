package com.example.skillcinema.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.skillcinema.data.local.room.model.CollectionDbo
import com.example.skillcinema.data.local.room.model.CollectionFilmDbo
import com.example.skillcinema.data.local.room.model.FilmDbo
import com.example.skillcinema.data.local.room.model.WatchedFilm

@Database(entities = [FilmDbo::class, CollectionDbo::class, CollectionFilmDbo::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun filmDao() : FilmDao
    abstract fun collectionDao() : CollectionDao
}