package com.example.skillcinema.domain.usecase

import com.example.skillcinema.DefaultCollections
import com.example.skillcinema.domain.model.LocalCollectionWithFilms
import com.example.skillcinema.domain.repository.FilmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCollectionWithFilmsUseCase @Inject constructor(private val filmRepository: FilmRepository) {
    fun execute(collectionId: Int): Flow<LocalCollectionWithFilms> {
        val flow = filmRepository.getCollectionWithFilms(collectionId).map { collection ->
            collection.films = collection.films.sortedByDescending { it.savedAt!! }
            collection
        }
        return if (collectionId == DefaultCollections.WATCHED)
            flow
        else
            flow.combine(filmRepository.getWatchedFilmsIds()) { collection, watchedIds ->
                collection.films.forEach { film -> film.isWatched = film.kinopoiskId in watchedIds }
                collection
            }
    }
}