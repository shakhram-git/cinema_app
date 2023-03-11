package com.example.skillcinema.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.skillcinema.DefaultCollections
import com.example.skillcinema.data.local.CollectionLocalSource
import com.example.skillcinema.data.local.FilmLocalSource
import com.example.skillcinema.data.local.room.model.CollectionDbo
import com.example.skillcinema.data.local.room.model.FilmDbo
import com.example.skillcinema.data.remote.*
import com.example.skillcinema.data.remote.retrofit.model.FilmDto
import com.example.skillcinema.data.remote.retrofit.model.FilmInfoDto
import com.example.skillcinema.domain.model.*
import com.example.skillcinema.domain.repository.FilmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FilmRepositoryImpl @Inject constructor(
    private val filmRemoteSource: FilmRemoteSource,
    private val filmsFilterRemoteSource: FilmsFilterRemoteSource,
    private val seriesRemoteSource: SeriesRemoteSource,
    private val filmPhotoRemoteSource: FilmPhotoRemoteSource,
    private val filmLocalSource: FilmLocalSource,
    private val collectionLocalSource: CollectionLocalSource
) : FilmRepository {


    override suspend fun getPremieresOfMonth(year: Int, month: String): List<Film> {
        val response = filmRemoteSource.getPremieresOfMonth(year, month)
        return response.map { mapFromFilmDto(it) }
    }

    override suspend fun getTop250Films(page: Int): List<Film> {
        val response = filmRemoteSource.getTop250Films(page)
        return response.map { mapFromFilmDto(it) }
    }

    override suspend fun getTopPopularFilms(page: Int): List<Film> {
        val response = filmRemoteSource.getTopPopularFilms(page)
        return response.map { mapFromFilmDto(it) }
    }

    override suspend fun getSeries(page: Int): List<Film> {
        val response = filmRemoteSource.getSeries(page)
        return response.map { mapFromFilmDto(it) }
    }

    override suspend fun getCountryGenreFilteredFilms(
        countryId: Long, genreId: Long, page: Int
    ): List<Film> {
        val response = filmRemoteSource.getCountryGenreFilteredFilms(countryId, genreId, page)
        return response.map { mapFromFilmDto(it) }
    }

    override suspend fun getSimilarFilms(filmId: Long): List<Film> {
        val response = filmRemoteSource.getSimilarFilms(filmId)
        return response.map { mapFromFilmDto(it) }
    }

    override fun getPagingFilmsList(filmsListType: FilmsListType): Flow<PagingData<Film>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                when (filmsListType) {
                    is FilmsListType.Filtered -> {
                        val pagingList = PagingFilmsType.FILTERED
                        pagingList.countryId = filmsListType.countryID
                        pagingList.genreId = filmsListType.genreId
                        FilmRemotePagingSource(
                            filmRemoteSource,
                            PagingFilmsType.FILTERED
                        )
                    }
                    FilmsListType.Popular ->
                        FilmRemotePagingSource(filmRemoteSource, PagingFilmsType.POPULAR)
                    FilmsListType.Series ->
                        FilmRemotePagingSource(filmRemoteSource, PagingFilmsType.SERIES)
                    FilmsListType.Top250 ->
                        FilmRemotePagingSource(filmRemoteSource, PagingFilmsType.TOP_250)
                    else -> throw IllegalStateException("Not a paging data")
                }
            }
        ).flow
            .map {
                it.map { film ->
                    mapFromFilmDto(film)
                }
            }
    }

    override fun searchPagingFilmsList(searchConfig: SearchConfig): Flow<PagingData<Film>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                FilmRemotePagingSource(filmRemoteSource, PagingFilmsType.SEARCH, searchConfig)
            }
        ).flow
            .map {
                it.map { film ->
                    mapFromFilmDto(film)
                }
            }

    }

    override suspend fun getFilmInfo(id: Long): FilmInfo {
        val result = filmRemoteSource.getFilmInfo(id)
        return mapFromFilmInfoDto(result)
    }


    override suspend fun getRandomFilmFilter(): FilmFilter {
        val list = filmsFilterRemoteSource.getFilmCountriesGenresList()
        val randomGenre = list.genres.random()
        val randomCountry = list.countries.random()
        return FilmFilter(
            randomGenre.id,
            randomGenre.genre,
            randomCountry.id,
            randomCountry.country
        )
    }

    override suspend fun getFilmsCountriesGenresList(): FilmCountriesGenresList {
        val list = filmsFilterRemoteSource.getFilmCountriesGenresList()
        return FilmCountriesGenresList(
            list.genres.map { Genre(it.id, it.genre) },
            list.countries.map { Country(it.id, it.country) }
        )
    }

    override suspend fun getSeasons(id: Long): Seasons {
        val seasons = seriesRemoteSource.getSeasons(id)
        return Seasons(
            seasons.total,
            seasons.items.map {
                Seasons.Season(
                    it.number,
                    it.episodes.map { episode ->
                        Episode(
                            episode.seasonNumber,
                            episode.episodeNumber,
                            episode.nameRu,
                            episode.nameEn,
                            episode.synopsis,
                            episode.releaseDate
                        )
                    }
                )
            }
        )
    }

    override suspend fun getFilmPhotos(filmId: Long, type: String?, page: Int): FilmPhotosList {
        val photosList = filmPhotoRemoteSource.getFilmPhotos(filmId, type, page)
        return FilmPhotosList(
            photosList.total,
            photosList.items.map { FilmPhoto(it.imageUrl, it.previewUrl) })
    }


    override fun getPagingFilmPhotos(
        filmId: Long,
        photosType: String?
    ): Flow<PagingData<FilmPhoto>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                FilmPhotoRemotePagingSource(filmPhotoRemoteSource, photosType, filmId)
            }
        ).flow
            .map {
                it.map { filmPhoto ->
                    FilmPhoto(filmPhoto.imageUrl, filmPhoto.previewUrl)
                }
            }
    }


    override fun getWatchedFilmsIds(): Flow<List<Long>> {
        return collectionLocalSource.getCollectionFilmsIds(DefaultCollections.WATCHED)
    }

    override fun getCollectionFilmsIds(collectionId: Int): Flow<List<Long>> {
        return collectionLocalSource.getCollectionFilmsIds(collectionId)
    }

    override suspend fun addFilmToCollection(film: Film, collectionId: Int, createdAt: Long) {
        filmLocalSource.addFilmToCollection(mapToFilmDbo(film), collectionId, createdAt)
    }

    override suspend fun removeFilmFromCollection(filmId: Long, collectionId: Int) {
        filmLocalSource.removeFilmFromCollection(filmId, collectionId)
    }

    override fun getAllCollectionsInfo(): Flow<List<LocalCollection>> {
        return collectionLocalSource.getAllCollections().map { list ->
            list.map { collection ->
                LocalCollection(
                    collection.collection.id!!,
                    collection.collection.name,
                    collection.collection.isDefault,
                    collection.films.map { it.film.id })
            }
        }
    }

    override suspend fun createNewCollection(collectionName: String): Long {
        return collectionLocalSource.createNewCollection(CollectionDbo(name = collectionName))
    }

    override fun getCollectionWithFilms(collectionId: Int): Flow<LocalCollectionWithFilms> {
        return collectionLocalSource.getCollectionWithFilmsFlow(collectionId).map { collection ->
            LocalCollectionWithFilms(
                collection.collection.id!!,
                collection.collection.name,
                collection.collection.isDefault,
                collection.films.map { filmDbo ->
                    val film = mapFromFilmDbo(filmDbo.film)
                    film.savedAt = filmDbo.relation.createdAt
                    film
                }
            )
        }
    }

    override suspend fun cleanCollection(collectionId: Int) {
        collectionLocalSource.cleanCollection(collectionId)
    }

    override suspend fun limitInterestedCollection(){
        collectionLocalSource.limitInterestedCollection()
    }

    override suspend fun deleteCollection(collectionId: Int){
        collectionLocalSource.deleteCollection(collectionId)
    }
    override suspend fun refreshLocalFilmsList(){
        filmLocalSource.refreshFilms()
    }

    private fun mapFromFilmDto(filmDto: FilmDto): Film {
        return Film(
            kinopoiskId = filmDto.kinopoiskId ?: filmDto.filmId!!,
            name = filmDto.nameRu ?: filmDto.nameEn ?: filmDto.nameOriginal!!,
            posterUrlPreview = filmDto.posterUrlPreview,
            genre = filmDto.genres?.firstOrNull()?.genre,
            premiereRu = filmDto.premiereRu,
            rating = filmDto.rating ?: filmDto.ratingKinopoisk?.toString(),
            isWatched = false,
            professionKey = filmDto.professionKey,
            year = filmDto.year
        )
    }

    private fun mapFromFilmDbo(filmDbo: FilmDbo): Film {
        return Film(
            kinopoiskId = filmDbo.id,
            name = filmDbo.name,
            posterUrlPreview = filmDbo.posterUrlPreview,
            genre = filmDbo.genre,
            premiereRu = null,
            rating = filmDbo.rating,
            isWatched = false,
            professionKey = null,
            year = null
        )
    }

    private fun mapToFilmDbo(film: Film): FilmDbo {
        return FilmDbo(
            id = film.kinopoiskId,
            name = film.name,
            posterUrlPreview = film.posterUrlPreview ?: "",
            genre = film.genre,
            rating = film.rating
        )
    }


    private fun mapFromFilmInfoDto(filmInfoDto: FilmInfoDto): FilmInfo {
        return FilmInfo(
            kinopoiskId = filmInfoDto.kinopoiskId,
            nameRu = filmInfoDto.nameRu,
            nameOriginal = filmInfoDto.nameOriginal,
            posterUrl = filmInfoDto.posterUrl,
            posterUrlPreview = filmInfoDto.posterUrlPreview,
            coverUrl = filmInfoDto.coverUrl,
            logoUrl = filmInfoDto.logoUrl,
            rating = filmInfoDto.ratingKinopoisk,
            webUrl = filmInfoDto.webUrl,
            year = filmInfoDto.year,
            filmLength = filmInfoDto.filmLength,
            countries = filmInfoDto.countries.map { it.country },
            genres = filmInfoDto.genres.map { it.genre },
            ratingAgeLimits = filmInfoDto.ratingAgeLimits,
            description = filmInfoDto.description,
            shortDescription = filmInfoDto.shortDescription,
            isSerial = filmInfoDto.serial,
            isCompleted = filmInfoDto.completed
        )
    }

}

enum class PagingFilmsType {
    TOP_250,
    POPULAR,
    FILTERED,
    SERIES,
    SEARCH;

    var countryId: Long? = null
    var genreId: Long? = null
}

