package com.example.skillcinema.data.remote

import com.example.skillcinema.data.remote.retrofit.model.FilmPhotosListDto
import com.example.skillcinema.data.remote.retrofit.KinopoiskApi
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FilmPhotoRemoteSource @Inject constructor(private val kinopoiskApi: KinopoiskApi) {
    suspend fun getFilmPhotos(filmId: Long, type: String?, page: Int): FilmPhotosListDto {
        val result = kinopoiskApi.getFilmPhotos(filmId, type, page)
        return if (result.isSuccessful) {
            result.body() ?: throw HttpException(result)
        } else throw HttpException(result)
    }
}