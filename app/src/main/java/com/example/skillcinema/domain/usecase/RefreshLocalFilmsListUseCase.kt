package com.example.skillcinema.domain.usecase

import com.example.skillcinema.domain.repository.FilmRepository
import javax.inject.Inject

class RefreshLocalFilmsListUseCase @Inject constructor(private val filmRepository: FilmRepository){
    suspend fun execute(){
        filmRepository.refreshLocalFilmsList()
    }
}