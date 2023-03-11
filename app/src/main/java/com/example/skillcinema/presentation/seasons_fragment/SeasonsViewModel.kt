package com.example.skillcinema.presentation.seasons_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.domain.model.Seasons
import com.example.skillcinema.domain.usecase.GetSeasonsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeasonsViewModel @Inject constructor(private val getSeasonsUseCase: GetSeasonsUseCase) :
    ViewModel() {

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

    private val _seasons = MutableStateFlow(Seasons(0, emptyList()))
    val seasons = _seasons.asStateFlow()

    fun getSeasons(filmId: Long) {
        viewModelScope.launch(mainExceptionHandler) {
            _state.value = State.LOADING
            _seasons.value = getSeasonsUseCase.execute(filmId)
            _state.value = State.SUCCESS
        }
    }

    enum class State {
        LOADING,
        SUCCESS,
        ERROR
    }
}