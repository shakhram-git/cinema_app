package com.example.skillcinema.domain.usecase

import com.example.skillcinema.domain.repository.FilmRepository
import javax.inject.Inject

class CleanCollectionUseCase @Inject constructor(private val filmRepository: FilmRepository) {
    suspend fun execute(collectionId: Int){
        filmRepository.cleanCollection(collectionId)
    }
}