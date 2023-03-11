package com.example.skillcinema.domain.usecase

import com.example.skillcinema.domain.model.StaffInfo
import com.example.skillcinema.domain.repository.StaffRepository
import javax.inject.Inject

class GetStaffInfoUseCase @Inject constructor(private val staffRepository: StaffRepository) {
    suspend fun execute(staffId: Long): StaffInfo {
        val result = staffRepository.getStaffInfo(staffId)
        val sortedFilmsList = result.films.sortedByDescending { it.rating }
        result.films = sortedFilmsList
        return result
    }
}