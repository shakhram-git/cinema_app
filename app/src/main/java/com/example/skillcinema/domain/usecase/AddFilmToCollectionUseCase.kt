package com.example.skillcinema.domain.usecase

import com.example.skillcinema.DefaultCollections
import com.example.skillcinema.domain.model.Film
import com.example.skillcinema.domain.repository.FilmRepository
import java.util.Calendar
import javax.inject.Inject

class AddFilmToCollectionUseCase @Inject constructor(private val filmRepository: FilmRepository) {
    suspend fun execute(film: Film, collectionId: Int) {
        filmRepository.addFilmToCollection(film, collectionId, Calendar.getInstance().timeInMillis)
        if (collectionId == DefaultCollections.INTERESTED) {
            filmRepository.limitInterestedCollection()
        }
        if (collectionId == DefaultCollections.WATCHED) {
            filmRepository.removeFilmFromCollection(film.kinopoiskId, DefaultCollections.DESIRED)
        }
    }
}