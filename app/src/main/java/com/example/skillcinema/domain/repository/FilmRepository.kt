package com.example.skillcinema.domain.repository

import androidx.paging.PagingData
import com.example.skillcinema.domain.model.*
import kotlinx.coroutines.flow.Flow

interface FilmRepository {

    suspend fun getPremieresOfMonth(year: Int, month: String): List<Film>

    suspend fun getRandomFilmFilter(): FilmFilter
    suspend fun getCountryGenreFilteredFilms(
        countryId: Long,
        genreId: Long,
        page: Int
    ): List<Film>

    suspend fun getTopPopularFilms(page: Int): List<Film>
    suspend fun getTop250Films(page: Int): List<Film>
    suspend fun getSeries(page: Int): List<Film>
    fun getWatchedFilmsIds(): Flow<List<Long>>
    suspend fun getFilmInfo(id: Long): FilmInfo
    suspend fun getSeasons(id: Long): Seasons
    fun getPagingFilmsList(filmsListType: FilmsListType): Flow<PagingData<Film>>
    suspend fun getFilmPhotos(filmId: Long, type: String?, page: Int): FilmPhotosList
    suspend fun getSimilarFilms(filmId: Long): List<Film>
    fun getPagingFilmPhotos(filmId: Long, photosType: String?): Flow<PagingData<FilmPhoto>>
    suspend fun getFilmsCountriesGenresList(): FilmCountriesGenresList
    fun searchPagingFilmsList(searchConfig: SearchConfig): Flow<PagingData<Film>>
    fun getCollectionFilmsIds(collectionId: Int): Flow<List<Long>>
    suspend fun removeFilmFromCollection(filmId: Long, collectionId: Int)
    fun getAllCollectionsInfo(): Flow<List<LocalCollection>>
    suspend fun createNewCollection(collectionName: String): Long
    fun getCollectionWithFilms(collectionId: Int): Flow<LocalCollectionWithFilms>
    suspend fun cleanCollection(collectionId: Int)
    suspend fun addFilmToCollection(film: Film, collectionId: Int, createdAt: Long)
    suspend fun limitInterestedCollection()
    suspend fun deleteCollection(collectionId: Int)
    suspend fun refreshLocalFilmsList()
}