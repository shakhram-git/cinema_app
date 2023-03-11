package com.example.skillcinema.domain.repository

interface UserPrefsRepository {

    suspend fun getIsFirstEnter(): Boolean

    suspend fun setIsFirstEnter(value: Boolean)
}