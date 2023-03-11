package com.example.skillcinema.domain.usecase

import com.example.skillcinema.domain.repository.FilmRepository
import javax.inject.Inject

class RemoveFilmFromCollectionUseCase @Inject constructor(private val filmRepository: FilmRepository) {
    suspend fun execute(filmId: Long, collectionId: Int) {
        filmRepository.removeFilmFromCollection(filmId, collectionId)
    }
}
