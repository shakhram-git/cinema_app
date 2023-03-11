package com.example.skillcinema.presentation.actor_info_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.domain.model.Film
import com.example.skillcinema.domain.model.StaffInfo
import com.example.skillcinema.domain.usecase.GetFilmInfoUseCase
import com.example.skillcinema.domain.usecase.GetStaffInfoUseCase
import com.example.skillcinema.domain.usecase.GetWatchedFilmsIds
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ActorInfoViewModel @Inject constructor(
    private val getStaffInfoUseCase: GetStaffInfoUseCase,
    getWatchedFilmsIds: GetWatchedFilmsIds,
    private val getFilmInfoUseCase: GetFilmInfoUseCase
) : ViewModel() {

    private val mainExceptionHandler = CoroutineExceptionHandler { _, _ ->
        viewModelScope.launch {
            _state.value = State.ERROR
            _error.send(true)
        }
    }
    private val secondaryExceptionHandler = CoroutineExceptionHandler { _, _ ->
        viewModelScope.launch {
            _error.send(true)
        }
    }

    private val watchedFilmsIds =
        getWatchedFilmsIds.execute().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _state = MutableStateFlow(State.LOADING)
    val state = _state.asStateFlow()

    private val _error = Channel<Boolean>()
    val error = _error.receiveAsFlow()


    private val _personInfo = MutableStateFlow(
        StaffInfo(
            0, "", "", "", "", emptyList()
        )
    )
    val personInfo = _personInfo.asStateFlow()

    private val _bestFilms = MutableStateFlow<List<Film>>(emptyList())
    val bestFilms: StateFlow<List<Film>> =
        combine(_bestFilms, watchedFilmsIds) { films, ids ->
            films.forEach { film ->
                film.isWatched = film.kinopoiskId in ids
            }
            films
        }.stateIn(viewModelScope, SharingStarted.Eagerly, _bestFilms.value)

    fun getPersonInfo(personId: Long) {
        viewModelScope.launch(mainExceptionHandler) {
            _state.value = State.LOADING
            val personInfo = getStaffInfoUseCase.execute(personId)
            personInfo.films = personInfo.films.distinctBy { it.kinopoiskId }
            _personInfo.value = personInfo
            _bestFilms.value = personInfo.films.take(20)
            _state.value = State.SUCCESS
        }
    }

    fun loadFilmPoster(filmId: Long) {
        viewModelScope.launch(secondaryExceptionHandler) {
            val filmInfo = getFilmInfoUseCase.execute(filmId)
            val filmList = _bestFilms.value.toMutableList()
            val index = filmList.indexOfFirst { it.kinopoiskId == filmInfo.kinopoiskId }
            val updateFilm = filmList[index].copy(posterUrlPreview = filmInfo.posterUrlPreview)
            filmList[index] = updateFilm
            _bestFilms.value = filmList
        }
    }


    enum class State {
        LOADING,
        SUCCESS,
        ERROR
    }
}