package com.example.skillcinema.presentation.collections_dialog_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.domain.model.Film
import com.example.skillcinema.domain.usecase.AddFilmToCollectionUseCase
import com.example.skillcinema.domain.usecase.CreateNewCollectionUseCase
import com.example.skillcinema.domain.usecase.GetAllCollectionsInfoUseCase
import com.example.skillcinema.domain.usecase.RemoveFilmFromCollectionUseCase
import com.example.skillcinema.presentation.home_fragment.HomeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionsDialogViewModel @Inject constructor(
    getAllCollectionsInfoUseCase: GetAllCollectionsInfoUseCase,
    private val addFilmToCollectionUseCase: AddFilmToCollectionUseCase,
    private val removeFilmFromCollectionUseCase: RemoveFilmFromCollectionUseCase
) : ViewModel() {





    val collections = getAllCollectionsInfoUseCase.execute().stateIn(
        viewModelScope, SharingStarted.Eagerly, emptyList()
    )



    fun addFilmToCollection(film: Film, collectionId: Int){
        viewModelScope.launch {
            addFilmToCollectionUseCase.execute(film, collectionId)
        }
    }

    fun removeFilmFromCollection(filmId: Long, collectionId: Int){
        viewModelScope.launch {
            removeFilmFromCollectionUseCase.execute(filmId, collectionId)
        }
    }

}