package com.example.skillcinema.presentation.new_collection_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.domain.model.Film
import com.example.skillcinema.domain.usecase.AddFilmToCollectionUseCase
import com.example.skillcinema.domain.usecase.CreateNewCollectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewCollectionDialogViewModel @Inject constructor(
    private val createNewCollectionUseCase: CreateNewCollectionUseCase,
    private val addFilmToCollectionUseCase: AddFilmToCollectionUseCase
) : ViewModel() {

    private val _error = Channel<Boolean>()
    val error = _error.receiveAsFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, _ ->
        viewModelScope.launch {
            _error.send(true)
        }
    }

    fun createNewCollection(collectionName: String, film: Film?) {
        viewModelScope.launch(exceptionHandler) {
            val id = createNewCollectionUseCase.execute(collectionName)
            film?.let {
                addFilmToCollectionUseCase.execute(it, id.toInt())
            }
            _error.send(false)
        }
    }

}