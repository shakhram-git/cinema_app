package com.example.skillcinema.presentation.list_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.skillcinema.domain.model.*
import com.example.skillcinema.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val getPremieresUseCase: GetPremieresUseCase,
    private val getPagingFilmsListUseCase: GetPagingFilmsListUseCase,
    private val getFilmStaffUseCase: GetFilmStaffUseCase,
    private val getSimilarFilmsUseCase: GetSimilarFilmsUseCase,
    private val getPersonsBestFilmsUseCase: GetPersonsBestFilmsUseCase,
    private val getFilmInfoUseCase: GetFilmInfoUseCase,
    private val getCollectionWithFilmsUseCase: GetCollectionWithFilmsUseCase,
    getWatchedFilmsIds: GetWatchedFilmsIds

) : ViewModel() {

    private val watchedFilmsIds =
        getWatchedFilmsIds.execute().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _state = MutableStateFlow(State.LOADING)
    val state = _state.asStateFlow()

    private val _error = Channel<Boolean>()
    val error = _error.receiveAsFlow()

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

    private val _filmsList = MutableStateFlow(emptyList<Film>())
    var filmsList: StateFlow<List<Film>> =
        combine(_filmsList, watchedFilmsIds) { films, watchedIds ->
            films.forEach { film ->
                film.isWatched = film.kinopoiskId in watchedIds
            }
            films
        }.stateIn(viewModelScope, SharingStarted.Eagerly, _filmsList.value)


    private val _staffList = MutableStateFlow(emptyList<Staff>())
    val staffList = _staffList.asStateFlow()


    var pagingFilmsList = emptyFlow<PagingData<Film>>()


    fun getList(type: ListType) {
        viewModelScope.launch(mainExceptionHandler) {
            _state.value = State.LOADING
            when (type) {
                is FilmsListType -> {
                    when (type.isPaging) {
                        false -> {
                            when (type) {
                                FilmsListType.Premieres -> _filmsList.value =
                                    getPremieresUseCase.execute().films
                                is FilmsListType.Similar -> _filmsList.value =
                                    getSimilarFilmsUseCase.execute(type.filmId)
                                is FilmsListType.PersonsBest -> _filmsList.value =
                                    getPersonsBestFilmsUseCase.execute(type.personId)
                                is FilmsListType.LocalCollectionFilms -> filmsList =
                                    getCollectionWithFilmsUseCase.execute(type.collectionId)
                                        .map { it.films }.stateIn(
                                        viewModelScope,
                                        SharingStarted.Eagerly,
                                        emptyList()
                                    )
                                else -> {}
                            }
                        }
                        true -> pagingFilmsList =
                            getPagingDataFlow(
                                getPagingFilmsListUseCase.execute(type).cachedIn(viewModelScope)
                            )
                    }
                }
                is StaffListType -> {
                    val result = getFilmStaffUseCase.execute(type.filmId)
                    when (type) {
                        is StaffListType.Actors ->
                            _staffList.value =
                                result.filter { it.professionKey == PROF_KEY_ACTOR }
                        is StaffListType.OtherStaff ->
                            _staffList.value =
                                result.filter { it.professionKey != PROF_KEY_ACTOR }
                    }
                }
            }
            _state.value = State.SUCCESS
        }
    }

    private fun getPagingDataFlow(flow: Flow<PagingData<Film>>): Flow<PagingData<Film>> {
        return flow.combine(watchedFilmsIds) { films, watchedIds ->
            val newPagingFilms = films.map { film ->
                film.isWatched = film.kinopoiskId in watchedIds
                return@map film
            }
            return@combine newPagingFilms
        }.cachedIn(viewModelScope)
    }

    fun loadFilmPoster(filmId: Long) {
        viewModelScope.launch(secondaryExceptionHandler) {
            viewModelScope.launch {
                val filmInfo = getFilmInfoUseCase.execute(filmId)
                val filmList = _filmsList.value.toMutableList()
                val index = filmList.indexOfFirst { it.kinopoiskId == filmInfo.kinopoiskId }
                val updateFilm = filmList[index].copy(posterUrlPreview = filmInfo.posterUrlPreview)
                filmList[index] = updateFilm
                _filmsList.value = filmList
            }
        }
    }

    companion object {
        const val PROF_KEY_ACTOR = "ACTOR"
    }

    enum class State {
        LOADING,
        SUCCESS,
        ERROR
    }

}