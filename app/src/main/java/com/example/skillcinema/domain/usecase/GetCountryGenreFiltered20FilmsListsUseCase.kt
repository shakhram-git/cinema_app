package com.example.skillcinema.domain.usecase

import com.example.skillcinema.domain.model.FilmFilter
import com.example.skillcinema.domain.model.FilmsList
import com.example.skillcinema.domain.model.FilmsListType
import com.example.skillcinema.domain.repository.FilmRepository
import java.util.*
import javax.inject.Inject

class GetCountryGenreFiltered20FilmsListsUseCase @Inject constructor(private val filmRepository: FilmRepository) {

    suspend fun execute(filmsListsAmount: Int): List<FilmsList> {
        val list = mutableListOf<FilmsList>()
        val brokenFilters = mutableListOf<FilmFilter>()
        for (i in 1..filmsListsAmount) {
            var filmFilter = filmRepository.getRandomFilmFilter()
            while (filmFilter in brokenFilters) {
                filmFilter = filmRepository.getRandomFilmFilter()
            }
            var filmsList = filmRepository.getCountryGenreFilteredFilms(
                filmFilter.countryId,
                filmFilter.genreId,
                1
            )
            while (
                list.any { filmFilter.countryId == (it.type as FilmsListType.Filtered).countryID } ||
                list.any { filmFilter.genreId == (it.type as FilmsListType.Filtered).genreId } ||
                filmsList.size < 5
            ) {
                brokenFilters.add(filmFilter)
                filmFilter = filmRepository.getRandomFilmFilter()
                while (filmFilter in brokenFilters) {
                    filmFilter = filmRepository.getRandomFilmFilter()
                }
                filmsList = filmRepository.getCountryGenreFilteredFilms(
                    filmFilter.countryId,
                    filmFilter.genreId,
                    1
                )
            }
            list.add(
                FilmsList(
                    FilmsListType.Filtered(
                        "${
                            filmFilter.genreName.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(
                                    Locale.getDefault()
                                ) else it.toString()
                            }
                        } ${filmFilter.countryName}",
                        filmFilter.countryId, filmFilter.genreId
                    ), filmsList
                )
            )
        }
        return list.toList()
    }


}