package com.example.skillcinema.data.remote

import com.example.skillcinema.data.remote.retrofit.model.StaffDto
import com.example.skillcinema.data.remote.retrofit.model.StaffInfoDto
import com.example.skillcinema.data.remote.retrofit.KinopoiskApi
import retrofit2.HttpException
import javax.inject.Inject

class StaffRemoteSource @Inject constructor(private val kinopoiskApi: KinopoiskApi) {
    suspend fun getFilmStaff(filmId: Long): List<StaffDto>{
        val response = kinopoiskApi.getFilmStaff(filmId)
        return if (response.isSuccessful)
            response.body() ?: emptyList()
        else emptyList()
    }

    suspend fun getStaffInfo(staffId: Long): StaffInfoDto {
        val response = kinopoiskApi.getStaffInfo(staffId)
        return if (response.isSuccessful)
            response.body() ?: throw HttpException(response)
        else throw HttpException(response)
    }
}