package com.example.skillcinema.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.skillcinema.domain.repository.UserPrefsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UserPrefsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
): UserPrefsRepository {
    override suspend fun getIsFirstEnter(): Boolean {
        return dataStore.data.first()[IS_FIRST_ENTER] ?: true
    }
    override suspend fun setIsFirstEnter(value: Boolean){
        dataStore.edit { preferences ->
            preferences[IS_FIRST_ENTER] = value
        }
    }

    companion object {
        val IS_FIRST_ENTER = booleanPreferencesKey("is_first_enter")
    }
}