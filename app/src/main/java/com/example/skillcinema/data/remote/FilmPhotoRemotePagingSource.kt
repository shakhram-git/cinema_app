package com.example.skillcinema.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.skillcinema.data.remote.retrofit.model.FilmPhotoDto

class FilmPhotoRemotePagingSource(
    private val filmPhotoSource: FilmPhotoRemoteSource,
    private val photosType: String?,
    private val filmId: Long,
    private val page: Int? = null
) : PagingSource<Int, FilmPhotoDto>() {
    override fun getRefreshKey(state: PagingState<Int, FilmPhotoDto>): Int = FIRST_PAGE

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FilmPhotoDto> {
        val page = params.key ?: page ?: FIRST_PAGE
        return try {
            val result = filmPhotoSource.getFilmPhotos(filmId, photosType, page)
            LoadResult.Page(
                result.items,
                if (page == FIRST_PAGE) null else page - 1,
                if (result.items.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    companion object {
        const val FIRST_PAGE = 1
    }

}