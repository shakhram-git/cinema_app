package com.example.skillcinema.domain.usecase

import com.example.skillcinema.domain.model.FilmPhoto
import com.example.skillcinema.domain.model.FilmPhotosList
import com.example.skillcinema.domain.repository.FilmRepository
import javax.inject.Inject

class Get20FilmPhotosUseCase @Inject constructor(private val filmRepository: FilmRepository) {
    suspend fun execute(filmId: Long, photosType: String? = null): FilmPhotosList {
        return filmRepository.getFilmPhotos(filmId, photosType, 1)
    }
}