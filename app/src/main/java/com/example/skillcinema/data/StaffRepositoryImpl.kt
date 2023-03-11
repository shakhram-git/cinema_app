package com.example.skillcinema.data

import com.example.skillcinema.data.remote.StaffRemoteSource
import com.example.skillcinema.data.remote.retrofit.model.FilmDto
import com.example.skillcinema.data.remote.retrofit.model.StaffDto
import com.example.skillcinema.domain.model.Film
import com.example.skillcinema.domain.model.Staff
import com.example.skillcinema.domain.model.StaffInfo
import com.example.skillcinema.domain.repository.StaffRepository
import javax.inject.Inject

class StaffRepositoryImpl @Inject constructor(private val staffSource: StaffRemoteSource) :
    StaffRepository {
    override suspend fun getFilmStaff(filmId: Long): List<Staff> {
        val stuff = staffSource.getFilmStaff(filmId)
        return stuff.map { mapFromStaffDto(it) }
    }

    override suspend fun getStaffInfo(staffId: Long): StaffInfo {
        val staffInfo = staffSource.getStaffInfo(staffId)
        return StaffInfo(
            staffInfo.personId,
            staffInfo.nameRu.ifBlank { staffInfo.nameEn },
            staffInfo.sex,
            staffInfo.posterUrl,
            staffInfo.profession,
            staffInfo.films?.map { mapFromFilmDto(it) } ?: emptyList()
        )
    }

    private fun mapFromStaffDto(staffDto: StaffDto): Staff {
        return Staff(
            staffId = staffDto.staffId,
            name = staffDto.nameRu.ifBlank { staffDto.nameEn },
            description = staffDto.description,
            posterUrl = staffDto.posterUrl,
            professionText = staffDto.professionText,
            professionKey = staffDto.professionKey
        )
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
}