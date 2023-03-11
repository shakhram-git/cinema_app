package com.example.skillcinema.domain.usecase

import com.example.skillcinema.domain.repository.FilmRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCollectionFilmsIdsUseCase @Inject constructor(private val filmRepository: FilmRepository) {
    fun execute(collectionId: Int): Flow<List<Long>> {
        return filmRepository.getCollectionFilmsIds(collectionId)
    }
}