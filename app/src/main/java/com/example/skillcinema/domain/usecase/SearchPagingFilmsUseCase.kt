package com.example.skillcinema.domain.usecase

import androidx.paging.PagingData
import com.example.skillcinema.domain.model.Film
import com.example.skillcinema.domain.model.SearchConfig
import com.example.skillcinema.domain.repository.FilmRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchPagingFilmsUseCase @Inject constructor(private val filmRepository: FilmRepository) {
    fun execute(searchConfig: SearchConfig): Flow<PagingData<Film>> {
        return filmRepository.searchPagingFilmsList(searchConfig)
    }
}