package com.example.skillcinema.domain.usecase

import com.example.skillcinema.domain.model.FilmCountriesGenresList
import com.example.skillcinema.domain.repository.FilmRepository
import javax.inject.Inject

class GetFilmsCountriesGenresListUseCase @Inject constructor(private val filmRepository: FilmRepository) {
    suspend fun execute(): FilmCountriesGenresList {
        return filmRepository.getFilmsCountriesGenresList()
    }
}