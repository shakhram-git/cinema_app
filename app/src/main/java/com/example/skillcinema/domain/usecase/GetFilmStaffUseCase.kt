package com.example.skillcinema.domain.usecase

import com.example.skillcinema.domain.model.Staff
import com.example.skillcinema.domain.repository.StaffRepository
import javax.inject.Inject

class GetFilmStaffUseCase @Inject constructor(private val staffRepository: StaffRepository) {
    suspend fun execute(filmId: Long): List<Staff> {
        return staffRepository.getFilmStaff(filmId)
    }
}