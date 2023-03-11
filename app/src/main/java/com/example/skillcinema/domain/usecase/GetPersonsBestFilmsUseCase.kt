package com.example.skillcinema.domain.usecase

import com.example.skillcinema.domain.model.Film
import com.example.skillcinema.domain.repository.StaffRepository
import javax.inject.Inject

class GetPersonsBestFilmsUseCase @Inject constructor(private val staffRepository: StaffRepository) {
    suspend fun execute(staffId: Long): List<Film> {
        return staffRepository.getStaffInfo(staffId).films.distinctBy { it.kinopoiskId }.sortedByDescending { it.rating }
    }
}