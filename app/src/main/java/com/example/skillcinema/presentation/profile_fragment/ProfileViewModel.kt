package com.example.skillcinema.presentation.profile_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.DefaultCollections
import com.example.skillcinema.domain.model.LocalCollection
import com.example.skillcinema.domain.model.LocalCollectionWithFilms
import com.example.skillcinema.domain.usecase.CleanCollectionUseCase
import com.example.skillcinema.domain.usecase.DeleteCollectionUseCase
import com.example.skillcinema.domain.usecase.GetAllCollectionsInfoUseCase
import com.example.skillcinema.domain.usecase.GetCollectionWithFilmsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCollectionWithFilmsUseCase: GetCollectionWithFilmsUseCase,
    private val getAllCollectionsInfoUseCase: GetAllCollectionsInfoUseCase,
    private val cleanCollectionUseCase: CleanCollectionUseCase,
    private val deleteCollectionUseCase: DeleteCollectionUseCase
) : ViewModel() {

    private val secondaryExceptionHandler = CoroutineExceptionHandler { _, _ ->
        viewModelScope.launch {
            _error.send(true)
        }
    }

    private val mainExceptionHandler = CoroutineExceptionHandler { _, _ ->
        viewModelScope.launch {
            _state.value = State.ERROR
            _error.send(true)
        }
    }

    private val _state = MutableStateFlow(State.LOADING)
    val state = _state.asStateFlow()

    private val _error = Channel<Boolean>()
    val error = _error.receiveAsFlow()

    lateinit var watchedFilms: StateFlow<LocalCollectionWithFilms>
    lateinit var interestedFilms: StateFlow<LocalCollectionWithFilms>
    lateinit var collections: StateFlow<List<LocalCollection>>

    init {
        getContent()
    }

    fun getContent() {
        viewModelScope.launch(mainExceptionHandler) {
            _state.value = State.LOADING
            watchedFilms =
                getCollectionWithFilmsUseCase.execute(DefaultCollections.WATCHED).stateIn(
                    viewModelScope, SharingStarted.Eagerly, DEFAULT_COLLECTION
                )
            interestedFilms =
                getCollectionWithFilmsUseCase.execute(DefaultCollections.INTERESTED).stateIn(
                    viewModelScope, SharingStarted.Eagerly, DEFAULT_COLLECTION
                )
            collections = getAllCollectionsInfoUseCase.execute().stateIn(
                viewModelScope, SharingStarted.Eagerly, emptyList()
            )
            _state.value = State.SUCCESS
        }
    }

    fun cleanCollection(collectionId: Int) {
        viewModelScope.launch(secondaryExceptionHandler) {
            cleanCollectionUseCase.execute(collectionId)
        }
    }

    fun deleteCollection(collectionId: Int) {
        viewModelScope.launch(secondaryExceptionHandler) {
            deleteCollectionUseCase.execute(collectionId)
        }
    }


    companion object {
        private val DEFAULT_COLLECTION = LocalCollectionWithFilms(
            0,
            "",
            false,
            emptyList()
        )
    }

    enum class State {
        LOADING,
        SUCCESS,
        ERROR
    }
}