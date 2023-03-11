package com.example.skillcinema.domain.usecase

import com.example.skillcinema.domain.model.FilmsList
import com.example.skillcinema.domain.model.FilmsListType
import com.example.skillcinema.domain.repository.FilmRepository
import javax.inject.Inject


class Get20SeriesUseCase @Inject constructor(private val filmRepository: FilmRepository) {
    suspend fun execute(): FilmsList {
        val list = filmRepository.getSeries(1)
        return FilmsList(FilmsListType.Series, list)
    }
}