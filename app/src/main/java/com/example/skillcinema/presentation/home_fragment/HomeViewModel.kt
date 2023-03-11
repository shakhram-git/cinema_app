package com.example.skillcinema.presentation.home_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.domain.model.FilmsList
import com.example.skillcinema.domain.usecase.GetPremieresUseCase
import com.example.skillcinema.domain.usecase.GetWatchedFilmsIds
import com.example.skillcinema.domain.usecase.GetCountryGenreFiltered20FilmsListsUseCase
import com.example.skillcinema.domain.usecase.Get20SeriesUseCase
import com.example.skillcinema.domain.usecase.Get20Top250FilmsUseCase
import com.example.skillcinema.domain.usecase.Get20TopPopularFilmsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPremieresUseCase: GetPremieresUseCase,
    private val get20Top250FilmsUseCase: Get20Top250FilmsUseCase,
    private val get20TopPopularFilmsUseCase: Get20TopPopularFilmsUseCase,
    private val get20CountryGenreFilteredFilmsUseCase: GetCountryGenreFiltered20FilmsListsUseCase,
    private val get20SeriesUseCase: Get20SeriesUseCase,
    getWatchedFilmsIds: GetWatchedFilmsIds,
) :
    ViewModel() {


    private val initState = HomeMainState(
        State.LOADING,
        emptyList()
    )

    private val watchedFilmsIds =
        getWatchedFilmsIds.execute().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _state = MutableStateFlow(initState)
    val state: StateFlow<HomeMainState> = combine(_state, watchedFilmsIds) { state, watchedIds ->
        state.filmsLists.forEach { filmsList ->
            filmsList.films.forEach { film ->
                film.isWatched = film.kinopoiskId in watchedIds
            }
        }
        state
    }.stateIn(viewModelScope, SharingStarted.Eagerly, _state.value)


    private val _error = Channel<Boolean>()
    val error = _error.receiveAsFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, _ ->
        viewModelScope.launch {
            _state.value = _state.value.copy(state = State.ERROR)
            _error.send(true)
        }
    }


    init {
        getContent()
    }

    fun getContent() {
        viewModelScope.launch(exceptionHandler) {
            _state.value = _state.value.copy(state = State.LOADING)
            val premieres = async {
                val result = getPremieresUseCase.execute()
                return@async FilmsList(result.type, result.films.take(20))
            }
            val popular = async {
                get20TopPopularFilmsUseCase.execute()
            }
            val filteredLists = async {
                get20CountryGenreFilteredFilmsUseCase.execute(FILTERED_LISTS_AMOUNT)
            }
            val top250 = async {
                get20Top250FilmsUseCase.execute()
            }
            val series = async {
                get20SeriesUseCase.execute()
            }
            _state.value = _state.value.copy(
                filmsLists = listOf(
                    premieres.await(),
                    popular.await(),
                    filteredLists.await().component1(),
                    top250.await(),
                    filteredLists.await().component2(),
                    series.await()
                )
            )
            _state.value = _state.value.copy(state = State.SUCCESS)
        }
    }


    companion object {
        const val FILTERED_LISTS_AMOUNT = 2
    }

    data class HomeMainState(
        val state: State,
        val filmsLists: List<FilmsList>
    )

    enum class State {
        LOADING,
        SUCCESS,
        ERROR
    }
}


