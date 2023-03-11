package com.example.skillcinema.domain.usecase

import com.example.skillcinema.domain.model.FilmsList
import com.example.skillcinema.domain.model.FilmsListType
import com.example.skillcinema.domain.repository.FilmRepository
import javax.inject.Inject

class Get20TopPopularFilmsUseCase @Inject constructor(private val filmRepository: FilmRepository) {

    suspend fun execute(): FilmsList {
        val result = filmRepository.getTopPopularFilms(1)
        return FilmsList(FilmsListType.Popular, result)
    }

}