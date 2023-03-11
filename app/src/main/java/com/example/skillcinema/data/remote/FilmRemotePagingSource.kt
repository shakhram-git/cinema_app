package com.example.skillcinema.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.skillcinema.data.PagingFilmsType

import com.example.skillcinema.data.remote.retrofit.model.FilmDto
import com.example.skillcinema.domain.model.SearchConfig

class FilmRemotePagingSource(
    private val filmSource: FilmRemoteSource,
    private val filmsListType: PagingFilmsType,
    private val searchConfig: SearchConfig? = null
) : PagingSource<Int, FilmDto>() {


    override fun getRefreshKey(state: PagingState<Int, FilmDto>): Int = FIRST_PAGE

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FilmDto> {
        val page = params.key ?: FIRST_PAGE
        return try {
            val result: List<FilmDto> = when (filmsListType) {
                PagingFilmsType.TOP_250 -> filmSource.getTop250Films(page)
                PagingFilmsType.POPULAR -> filmSource.getTopPopularFilms(page)
                PagingFilmsType.FILTERED -> filmSource.getCountryGenreFilteredFilms(
                    filmsListType.countryId!!,
                    filmsListType.genreId!!,
                    page
                )
                PagingFilmsType.SERIES -> filmSource.getSeries(page)
                PagingFilmsType.SEARCH ->
                    filmSource.searchFilms(
                        searchConfig!!.country?.id,
                        searchConfig.genre?.id,
                        searchConfig.order,
                        searchConfig.type,
                        searchConfig.ratingFrom,
                        searchConfig.ratingTo,
                        searchConfig.yearFrom,
                        searchConfig.yearTo,
                        searchConfig.keyword,
                        page
                    )

            }
            LoadResult.Page(
                result,
                if (page == FIRST_PAGE) null else page - 1,
                if (result.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    companion object {
        const val FIRST_PAGE = 1
    }
}