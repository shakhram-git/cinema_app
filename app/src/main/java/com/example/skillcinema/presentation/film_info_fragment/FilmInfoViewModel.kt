package com.example.skillcinema.presentation.film_info_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.DefaultCollections
import com.example.skillcinema.domain.model.Film
import com.example.skillcinema.domain.model.FilmInfo
import com.example.skillcinema.domain.model.FilmPhotosList
import com.example.skillcinema.domain.model.Staff
import com.example.skillcinema.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilmInfoViewModel @Inject constructor(
    private val getFilmInfoUseCase: GetFilmInfoUseCase,
    private val getFilmStaffUseCase: GetFilmStaffUseCase,
    private val get20FilmPhotosUseCase: Get20FilmPhotosUseCase,
    private val getSimilarFilmsUseCase: GetSimilarFilmsUseCase,
    getCollectionFilmsIdsUseCase: GetCollectionFilmsIdsUseCase,
    private val addFilmToCollectionUseCase: AddFilmToCollectionUseCase,
    private val removeFilmFromCollectionUseCase: RemoveFilmFromCollectionUseCase
) : ViewModel() {

    private val initialFilmInfo = FilmInfo(
        kinopoiskId = 0,
        nameRu = "",
        nameOriginal = "",
        posterUrl = "",
        posterUrlPreview = "",
        coverUrl = "",
        logoUrl = "",
        rating = 0.0,
        webUrl = "",
        year = 0,
        filmLength = 0,
        countries = emptyList(),
        genres = emptyList(),
        ratingAgeLimits = "",
        description = "",
        shortDescription = "",
        isSerial = false,
        isCompleted = false
    )

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

    val watchedFilmsIds =
        getCollectionFilmsIdsUseCase.execute(DefaultCollections.WATCHED)
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val favouriteFilmsIds =
        getCollectionFilmsIdsUseCase.execute(DefaultCollections.FAVOURITE)
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val desiredFilmsIds =
        getCollectionFilmsIdsUseCase.execute(DefaultCollections.DESIRED)
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _state = MutableStateFlow(State.LOADING)
    val state = _state.asStateFlow()
    private val _error = Channel<Boolean>()
    val error = _error.receiveAsFlow()

    private val _filmInfo = MutableStateFlow(initialFilmInfo)
    val filmInfo = _filmInfo.asStateFlow()

    private val _actors = MutableStateFlow<List<Staff>>(emptyList())
    val actors = _actors.asStateFlow()

    private val _otherStaff = MutableStateFlow<List<Staff>>(emptyList())
    val otherStaff = _otherStaff.asStateFlow()

    private val _galleryPhotos = MutableStateFlow(FilmPhotosList(0, emptyList()))
    val galleryPhotos = _galleryPhotos.asStateFlow()

    private val _similarFilms = MutableStateFlow<List<Film>>(emptyList())
    val similarFilms: StateFlow<List<Film>> =
        combine(_similarFilms, watchedFilmsIds) { films, watchedIds ->
            films.forEach { film ->
                film.isWatched = film.kinopoiskId in watchedIds
            }
            films
        }.stateIn(viewModelScope, SharingStarted.Eagerly, _similarFilms.value)

    fun getFilmInfo(id: Long) {
        viewModelScope.launch(mainExceptionHandler) {
            _state.value = State.LOADING
            val filmInfo = async {
                getFilmInfoUseCase.execute(id)
            }
            val filmStuff = async {
                getFilmStaffUseCase.execute(id)
            }
            val galleryPhotos = async {
                get20FilmPhotosUseCase.execute(id)
            }

            val similarFilms = async {
                getSimilarFilmsUseCase.execute(id)
            }
            _filmInfo.value = filmInfo.await()
            _galleryPhotos.value = galleryPhotos.await()
            _similarFilms.value = similarFilms.await()
            val stuff = filmStuff.await()
            _actors.value = stuff.filter { it.professionKey == PROF_KEY_ACTOR }
            _otherStaff.value = stuff.filter { it.professionKey != PROF_KEY_ACTOR }
            addFilmToCollectionUseCase.execute(
                transformToFilm(_filmInfo.value), DefaultCollections.INTERESTED
            )
            _state.value = State.SUCCESS
        }
    }

    fun addToCollection(collectionId: Int) {
        viewModelScope.launch(secondaryExceptionHandler) {
            addFilmToCollectionUseCase.execute(transformToFilm(_filmInfo.value), collectionId)
        }
    }

    fun removeFromCollection(collectionId: Int) {
        viewModelScope.launch(secondaryExceptionHandler) {
            removeFilmFromCollectionUseCase.execute(_filmInfo.value.kinopoiskId, collectionId)
        }
    }

    private fun transformToFilm(filmInfo: FilmInfo): Film {
        return Film(
            filmInfo.kinopoiskId,
            filmInfo.nameRu ?: filmInfo.nameOriginal!!,
            posterUrlPreview = filmInfo.posterUrlPreview,
            genre = filmInfo.genres.firstOrNull(),
            premiereRu = null,
            rating = filmInfo.rating.toString(),
            isWatched = false,
            professionKey = null,
            year = filmInfo.year
        )
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