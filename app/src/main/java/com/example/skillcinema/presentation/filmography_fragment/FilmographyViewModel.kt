package com.example.skillcinema.presentation.filmography_fragment

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
class FilmographyViewModel @Inject constructor(
    private val getStaffInfoUseCase: GetStaffInfoUseCase,
    private val getFilmInfoUseCase: GetFilmInfoUseCase,
    getWatchedFilmsIds: GetWatchedFilmsIds
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

    private val _state = MutableStateFlow(State.LOADING)
    val state = _state.asStateFlow()
    private val _error = Channel<Boolean>()
    val error = _error.receiveAsFlow()

    private val _personInfo = MutableStateFlow(StaffInfo(0, "", "", "", "", emptyList()))
    val personInfo = _personInfo.asStateFlow()

    private val _professionsList = MutableStateFlow(emptyList<Profession>())
    val professionsList = _professionsList.asStateFlow()

    private val profession = MutableStateFlow<Profession?>(null)

    private val watchedFilmsIds =
        getWatchedFilmsIds.execute().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _filmsList = MutableStateFlow(emptyList<Film>())
    val filmsList: StateFlow<List<Film>> =
        combine(_filmsList, profession, watchedFilmsIds) { films, prof, watchedIds ->
            val result = prof?.let {
                return@let films.filter { film ->
                    prof.keyWords.any { it == film.professionKey }
                }.distinctBy { it.kinopoiskId }
            } ?: films.distinctBy { it.kinopoiskId }
            result.forEach { film ->
                film.isWatched = film.kinopoiskId in watchedIds
            }
            return@combine result
        }.stateIn(viewModelScope, SharingStarted.Eagerly, _filmsList.value)


    fun getPersonInfo(personId: Long) {
        viewModelScope.launch(mainExceptionHandler) {
            _state.value = State.LOADING
            val personInfo = getStaffInfoUseCase.execute(personId)
            val professionsList =
                personInfo.films.map { it.professionKey }.distinct().mapNotNull { keyWord ->
                    Profession.values().firstOrNull { profession ->
                        profession.keyWords.any { it == keyWord }
                    }
                }.distinct()
            professionsList.forEach { profession ->
                profession.isFemale = personInfo.sex == FEMALE_KEY
                profession.filmAmount = personInfo.films.filter { film ->
                    profession.keyWords.any { it == film.professionKey }
                }.distinctBy { it.kinopoiskId }.size
            }
            _professionsList.value = professionsList
            _filmsList.value = personInfo.films
            _personInfo.value = personInfo
            _state.value = State.SUCCESS
        }
    }

    fun loadFilmPoster(filmId: Long) {
        viewModelScope.launch(secondaryExceptionHandler) {
            val filmInfo = getFilmInfoUseCase.execute(filmId)
            val filmList = _filmsList.value.toMutableList()
            val indexes = filmList.mapIndexed { index, film ->
                if (film.kinopoiskId == filmInfo.kinopoiskId)
                    index else null
            }.filterNotNull()
            indexes.forEach { index ->
                val updateFilm = filmList[index].copy(posterUrlPreview = filmInfo.posterUrlPreview)
                filmList[index] = updateFilm
            }
            _filmsList.value = filmList
        }
    }

    fun filterByProf(prof: Profession) {
        viewModelScope.launch {
            profession.value = prof
        }
    }

    enum class Profession(val keyWords: List<String>) {
        ACTOR(listOf("ACTOR")),
        WRITER(listOf("WRITER")),
        SELF_PLAYING_ACTOR(listOf("HIMSELF", "HERSELF")),
        IN_TITLES(listOf("HRONO_TITR_MALE", "HRONO_TITR_FEMALE")),
        DIRECTOR(listOf("DIRECTOR")),
        PRODUCER(listOf("PRODUCER")),
        OPERATOR(listOf("OPERATOR")),
        COMPOSER(listOf("COMPOSER"));

        var filmAmount = 0
        var isFemale = false
    }

    companion object {
        const val FEMALE_KEY = "FEMALE"
    }

    enum class State {
        LOADING,
        SUCCESS,
        ERROR
    }


}