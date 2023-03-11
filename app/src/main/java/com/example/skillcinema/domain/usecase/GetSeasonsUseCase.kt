package com.example.skillcinema.domain.usecase

import com.example.skillcinema.domain.model.Seasons
import com.example.skillcinema.domain.repository.FilmRepository
import javax.inject.Inject

class GetSeasonsUseCase @Inject constructor(private val filmRepository: FilmRepository) {
    suspend fun execute(filmId: Long): Seasons {
        return filmRepository.getSeasons(filmId)
    }
}