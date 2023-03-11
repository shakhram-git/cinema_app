package com.example.skillcinema.domain.usecase

import androidx.paging.PagingData
import com.example.skillcinema.domain.model.Film
import com.example.skillcinema.domain.model.FilmsListType
import com.example.skillcinema.domain.repository.FilmRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPagingFilmsListUseCase @Inject constructor(private val filmRepository: FilmRepository) {
    fun execute(filmsType: FilmsListType): Flow<PagingData<Film>> {
        if (filmsType.isPaging)
            return filmRepository.getPagingFilmsList(filmsType)
        else throw IllegalStateException("Not a paging data")
    }
}