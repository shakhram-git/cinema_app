package com.example.skillcinema.domain.usecase

import com.example.skillcinema.domain.model.Film
import com.example.skillcinema.domain.repository.FilmRepository
import javax.inject.Inject

class GetSimilarFilmsUseCase @Inject constructor(private val filmRepository: FilmRepository) {
    suspend fun execute(filmId: Long): List<Film> {
        return filmRepository.getSimilarFilms(filmId)
    }
}