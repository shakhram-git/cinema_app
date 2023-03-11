package com.example.skillcinema.domain.usecase

import com.example.skillcinema.domain.repository.FilmRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWatchedFilmsIds @Inject constructor(private val filmRepository: FilmRepository){

    fun execute() : Flow<List<Long>> {
        return filmRepository.getWatchedFilmsIds()
    }
}