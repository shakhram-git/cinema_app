package com.example.skillcinema.data.remote.retrofit

import com.example.skillcinema.data.remote.retrofit.model.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface KinopoiskApi {
    @Headers("X-API-KEY: $KINOPOISK_API_KEY")
    @GET("/api/v2.2/films/premieres")
    suspend fun getPremieresOfMonth(
        @Query("year") year: Int,
        @Query("month") month: String
    ): Response<FilmsListDto>

    @Headers("X-API-KEY: $KINOPOISK_API_KEY")
    @GET("/api/v2.2/films/top")
    suspend fun getTopFilms(
        @Query("type") type: String,
        @Query("page") page: Int
    ): Response<FilmsListDto>

    @Headers("X-API-KEY: $KINOPOISK_API_KEY")
    @GET("/api/v2.2/films/filters")
    suspend fun getFilmFilters(): Response<FilmCountriesGenresListDto>

    @Headers("X-API-KEY: $KINOPOISK_API_KEY")
    @GET("/api/v2.2/films")
    suspend fun searchFilms(
        @Query("countries") countryId: Long?,
        @Query("genres") genreId: Long?,
        @Query("order") order: String,
        @Query("type") type: String,
        @Query("ratingFrom") ratingFrom: Int,
        @Query("ratingTo") ratingTo: Int,
        @Query("yearFrom") yearFrom: Int,
        @Query("yearTo") yearTo: Int,
        @Query("keyword") keyword: String?,
        @Query("page") page: Int
    ): Response<FilmsListDto>

    @Headers("X-API-KEY: $KINOPOISK_API_KEY")
    @GET("/api/v2.2/films/{id}")
    suspend fun getFilmInfo(
        @Path("id") id: Long
    ): Response<FilmInfoDto>

    @Headers("X-API-KEY: $KINOPOISK_API_KEY")
    @GET("/api/v2.2/films/{id}/seasons")
    suspend fun getSeasons(
        @Path("id") id: Long
    ): Response<SeasonsDto>

    @Headers("X-API-KEY: $KINOPOISK_API_KEY")
    @GET("/api/v1/staff")
    suspend fun getFilmStaff(
        @Query("filmId") id: Long
    ): Response<List<StaffDto>>

    @Headers("X-API-KEY: $KINOPOISK_API_KEY")
    @GET("/api/v1/staff/{id}")
    suspend fun getStaffInfo(
        @Path("id") id: Long
    ): Response<StaffInfoDto>

    @Headers("X-API-KEY: $KINOPOISK_API_KEY")
    @GET("/api/v2.2/films/{id}/images")
    suspend fun getFilmPhotos(
        @Path("id") id: Long,
        @Query("type") type: String?,
        @Query("page") page: Int
    ): Response<FilmPhotosListDto>

    @Headers("X-API-KEY: $KINOPOISK_API_KEY")
    @GET("/api/v2.2/films/{id}/similars")
    suspend fun getSimilarFilms(
        @Path("id") id: Long
    ): Response<FilmsListDto>


    companion object {
        const val KINOPOISK_API_KEY = "4bc90460-f72a-4824-b8cb-cf5d0fe22ab3"
    }


}