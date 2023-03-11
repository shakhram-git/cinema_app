package com.example.skillcinema.data.remote

import com.example.skillcinema.data.remote.retrofit.model.SeasonsDto
import com.example.skillcinema.data.remote.retrofit.KinopoiskApi
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeriesRemoteSource @Inject constructor(private val kinopoiskApi: KinopoiskApi) {
    suspend fun getSeasons(id: Long): SeasonsDto {
        val result = kinopoiskApi.getSeasons(id)
        return if (result.isSuccessful) {
            result.body() ?: throw HttpException(result)
        } else throw HttpException(result)
    }
}