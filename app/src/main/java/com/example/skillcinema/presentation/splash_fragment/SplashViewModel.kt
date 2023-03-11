package com.example.skillcinema.presentation.splash_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.domain.usecase.GetIsFirstEnterUseCase
import com.example.skillcinema.domain.usecase.RefreshLocalFilmsListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getIsFirstEnterUseCase: GetIsFirstEnterUseCase,
    private val refreshLocalFilmsListUseCase: RefreshLocalFilmsListUseCase
    ) : ViewModel() {

    private val _isFirstEnter = MutableStateFlow<Boolean?>(null)
    val isFirstEnter = _isFirstEnter.asStateFlow()

    init {
        refreshFilmsList()
        getIsFirstEnter()
    }

    private fun getIsFirstEnter(){
        viewModelScope.launch {
            _isFirstEnter.value = getIsFirstEnterUseCase.execute()
        }
    }

    private fun refreshFilmsList(){
        viewModelScope.launch {
            refreshLocalFilmsListUseCase.execute()
        }
    }
}