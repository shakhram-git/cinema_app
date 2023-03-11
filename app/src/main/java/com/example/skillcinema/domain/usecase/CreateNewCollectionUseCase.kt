package com.example.skillcinema.domain.usecase

import com.example.skillcinema.domain.repository.FilmRepository
import javax.inject.Inject

class CreateNewCollectionUseCase @Inject constructor(private val filmRepository: FilmRepository) {
    suspend fun execute(collectionName: String): Long {
        return filmRepository.createNewCollection(collectionName)
    }
}