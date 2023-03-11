package com.example.skillcinema.domain.usecase

import androidx.paging.PagingData
import com.example.skillcinema.domain.model.FilmPhoto
import com.example.skillcinema.domain.repository.FilmRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPagingFilmPhotosUseCase @Inject constructor(private val filmRepository: FilmRepository) {
    fun execute(filmId:Long, photosType:String? = null): Flow<PagingData<FilmPhoto>> {
        return filmRepository.getPagingFilmPhotos(filmId, photosType)
    }
}