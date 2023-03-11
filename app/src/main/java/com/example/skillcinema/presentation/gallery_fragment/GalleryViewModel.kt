package com.example.skillcinema.presentation.gallery_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.skillcinema.PhotoCategory
import com.example.skillcinema.domain.model.FilmPhoto
import com.example.skillcinema.domain.usecase.Get20FilmPhotosUseCase
import com.example.skillcinema.domain.usecase.GetPagingFilmPhotosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val getPagingFilmPhotosUseCase: GetPagingFilmPhotosUseCase,
    private val get20FilmPhotosUseCase: Get20FilmPhotosUseCase
) : ViewModel() {

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

    var pagingPhotos = emptyFlow<PagingData<FilmPhoto>>()


    private val _photoCategories = MutableStateFlow(emptyList<PhotoCategory>())
    val photoCategories = _photoCategories.asStateFlow()

    fun getPagingFilmPhotos(filmId: Long, photosType: String) {
        pagingPhotos = getPagingFilmPhotosUseCase.execute(filmId, photosType).cachedIn(viewModelScope)
    }

    fun getPhotoCategories(filmId: Long) {
        viewModelScope.launch(mainExceptionHandler) {
            _state.value = State.LOADING
            val photoCategories = PhotoCategory.values()
            photoCategories.forEach { category ->
                category.amount =
                    get20FilmPhotosUseCase.execute(filmId, category.keyString).total
            }
            _photoCategories.value = photoCategories.toList().filter { it.amount > 0 }
            _state.value = State.SUCCESS
        }
    }

    enum class State {
        LOADING,
        SUCCESS,
        ERROR
    }


}