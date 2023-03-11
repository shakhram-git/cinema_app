package com.example.skillcinema.data.remote

import com.example.skillcinema.data.remote.retrofit.model.FilmCountriesGenresListDto
import com.example.skillcinema.data.remote.retrofit.KinopoiskApi
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FilmsFilterRemoteSource @Inject constructor(private val kinopoiskApi: KinopoiskApi) {

    suspend fun getFilmCountriesGenresList(): FilmCountriesGenresListDto {
        val result = kinopoiskApi.getFilmFilters()
        return if (result.isSuccessful) {
            result.body() ?: throw HttpException(result)
        } else {
            throw HttpException(result)
        }
    }

}