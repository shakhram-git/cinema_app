package com.example.skillcinema.data.local

import androidx.room.Transaction
import com.example.skillcinema.data.local.room.FilmDao
import com.example.skillcinema.data.local.room.model.CollectionFilmDbo
import com.example.skillcinema.data.local.room.model.FilmDbo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FilmLocalSource @Inject constructor(private val filmDao: FilmDao) {

    @Transaction
    suspend fun addFilmToCollection(filmDbo: FilmDbo, collectionId: Int, createdAt: Long) {
        filmDao.insert(filmDbo)
        filmDao.addFilmToCollection(CollectionFilmDbo(collectionId, filmDbo.id, createdAt))
    }

    suspend fun removeFilmFromCollection(filmId: Long, collectionId: Int) {
        filmDao.removeFilmFromCollection(collectionId, filmId)
    }

    suspend fun refreshFilms(){
        filmDao.refreshFilms()
    }
}