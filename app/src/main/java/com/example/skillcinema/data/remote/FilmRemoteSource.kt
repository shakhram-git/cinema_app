package com.example.skillcinema.data.remote

import com.example.skillcinema.data.remote.retrofit.model.FilmDto
import com.example.skillcinema.data.remote.retrofit.model.FilmInfoDto
import com.example.skillcinema.data.remote.retrofit.KinopoiskApi
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FilmRemoteSource @Inject constructor(private val kinopoiskApi: KinopoiskApi) {

    suspend fun getPremieresOfMonth(year: Int, month: String): List<FilmDto> {
        val result = kinopoiskApi.getPremieresOfMonth(year, month)
        return if (result.isSuccessful) {
            result.body()?.items ?: result.body()?.films ?: throw HttpException(result)
        } else throw HttpException(result)
    }

    suspend fun getTop250Films(page: Int): List<FilmDto> {
        val result = kinopoiskApi.getTopFilms(TOP_250, page)
        return if (result.isSuccessful) {
            result.body()?.items ?: result.body()?.films ?: throw HttpException(result)
        } else throw HttpException(result)
    }

    suspend fun getTopPopularFilms(page: Int): List<FilmDto> {
        val result = kinopoiskApi.getTopFilms(TOP_POPULAR, page)
        return if (result.isSuccessful) {
            result.body()?.items ?: result.body()?.films ?: throw HttpException(result)
        } else throw HttpException(result)

    }

    suspend fun getSimilarFilms(filmId: Long): List<FilmDto>{
        val result = kinopoiskApi.getSimilarFilms(filmId)
        return if (result.isSuccessful) {
            result.body()?.items ?: result.body()?.films ?: throw HttpException(result)
        } else throw HttpException(result)
    }

    suspend fun getCountryGenreFilteredFilms(
        countryId: Long,
        genreId: Long,
        page: Int
    ): List<FilmDto> {
        return searchFilms(countryId = countryId, genreId = genreId, page = page)
    }

    suspend fun getSeries(page: Int): List<FilmDto> {
        return searchFilms(page = page, type = SERIES_TYPE)
    }


    suspend fun getFilmInfo(id: Long): FilmInfoDto {
        val result = kinopoiskApi.getFilmInfo(id)
        return if (result.isSuccessful) {
            result.body() ?: throw HttpException(result)
        } else throw HttpException(result)
    }

    suspend fun searchFilms(
        countryId: Long? = null,
        genreId: Long? = null,
        order: String = DEF_ORDER,
        type: String = DEF_TYPE,
        ratingFrom: Int = DEF_RATING_FROM,
        ratingTo: Int = DEF_RATING_TO,
        yearFrom: Int = DEF_YEAR_FROM,
        yearTo: Int = DEF_YEAR_TO,
        keyword: String? = null,
        page: Int
    ): List<FilmDto> {
        val result = kinopoiskApi.searchFilms(
            countryId, genreId, order, type, ratingFrom, ratingTo, yearFrom, yearTo, keyword, page
        )
        return if (result.isSuccessful) {
            result.body()?.items ?: result.body()?.films ?: throw HttpException(result)
        } else {
            throw HttpException(result)
        }
    }


    companion object {
        const val DEF_ORDER = "RATING"
        const val DEF_TYPE = "ALL"
        const val DEF_RATING_FROM = 0
        const val DEF_RATING_TO = 10
        const val DEF_YEAR_FROM = 1000
        const val DEF_YEAR_TO = 3000
        const val TOP_250 = "TOP_250_BEST_FILMS"
        const val TOP_POPULAR = "TOP_100_POPULAR_FILMS"
        const val SERIES_TYPE = "TV_SERIES"

    }


}