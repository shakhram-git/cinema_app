package com.example.skillcinema.domain.usecase

import com.example.skillcinema.domain.repository.UserPrefsRepository
import javax.inject.Inject

class GetIsFirstEnterUseCase @Inject constructor(private val userPrefsRepository: UserPrefsRepository) {
    suspend fun execute(): Boolean{
        val result = userPrefsRepository.getIsFirstEnter()
        if (result) {
            userPrefsRepository.setIsFirstEnter(false)
        }
        return result
    }

}