package com.example.skillcinema.di

import com.example.skillcinema.data.FilmRepositoryImpl
import com.example.skillcinema.data.StaffRepositoryImpl
import com.example.skillcinema.data.UserPrefsRepositoryImpl
import com.example.skillcinema.domain.repository.FilmRepository
import com.example.skillcinema.domain.repository.StaffRepository
import com.example.skillcinema.domain.repository.UserPrefsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class DomainBindModule {
    @Binds
    @Singleton
    abstract fun bindFilmRepository(filmRepositoryImpl: FilmRepositoryImpl): FilmRepository

    @Binds
    @Singleton
    abstract fun bindUserPrefsRepository(userPrefsRepositoryImpl: UserPrefsRepositoryImpl): UserPrefsRepository

    @Binds
    @Singleton
    abstract fun bindStuffRepository(stuffRepositoryImpl: StaffRepositoryImpl): StaffRepository

}