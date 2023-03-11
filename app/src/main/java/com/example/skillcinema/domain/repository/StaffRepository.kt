package com.example.skillcinema.domain.repository

import com.example.skillcinema.domain.model.Staff
import com.example.skillcinema.domain.model.StaffInfo

interface StaffRepository {
    suspend fun getFilmStaff(filmId: Long): List<Staff>
    suspend fun getStaffInfo(staffId: Long): StaffInfo
}