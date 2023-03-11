package com.example.skillcinema.domain.usecase

import com.example.skillcinema.domain.model.FilmInfo
import com.example.skillcinema.domain.model.SeriesInfo
import com.example.skillcinema.domain.repository.FilmRepository
import javax.inject.Inject

class GetFilmInfoUseCase @Inject constructor(private val filmRepository: FilmRepository) {
    suspend fun execute(id: Long): FilmInfo{
        val filmInfo = filmRepository.getFilmInfo(id)
        if (filmInfo.isSerial){
            val seasons = filmRepository.getSeasons(id)
            var episodes = 0
            seasons.seasons.forEach {
                repeat(it.episodes.size) { episodes++ }
            }
            filmInfo.seriesInfo =  SeriesInfo(
                seasons = seasons.seasons.lastOrNull()?.number,
                episodes = episodes
            )
        }
        return filmInfo
    }
}