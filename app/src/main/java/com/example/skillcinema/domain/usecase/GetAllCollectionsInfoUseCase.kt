package com.example.skillcinema.domain.usecase

import com.example.skillcinema.domain.model.LocalCollection
import com.example.skillcinema.domain.repository.FilmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

class GetAllCollectionsInfoUseCase @Inject constructor(private val filmRepository: FilmRepository) {
    fun execute(): Flow<List<LocalCollection>>{
        return filmRepository.getAllCollectionsInfo()
    }
}