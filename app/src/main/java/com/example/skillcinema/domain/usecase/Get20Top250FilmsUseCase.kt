package com.example.skillcinema.domain.usecase

import com.example.skillcinema.domain.model.FilmsList
import com.example.skillcinema.domain.model.FilmsListType
import com.example.skillcinema.domain.repository.FilmRepository
import javax.inject.Inject


class Get20Top250FilmsUseCase @Inject constructor(private val filmRepository: FilmRepository) {
    suspend fun execute(): FilmsList {
        val result = filmRepository.getTop250Films(1)
        return FilmsList(FilmsListType.Top250, result)
    }

}