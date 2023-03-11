package com.example.skillcinema.presentation.search_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.example.skillcinema.IsCheckedCoverUI
import com.example.skillcinema.domain.model.Country
import com.example.skillcinema.domain.model.Film
import com.example.skillcinema.domain.model.Genre
import com.example.skillcinema.domain.model.SearchConfig
import com.example.skillcinema.domain.usecase.GetFilmsCountriesGenresListUseCase
import com.example.skillcinema.domain.usecase.GetWatchedFilmsIds
import com.example.skillcinema.domain.usecase.SearchPagingFilmsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getFilmsCountriesGenresListUseCase: GetFilmsCountriesGenresListUseCase,
    private val searchPagingFilmsUseCase: SearchPagingFilmsUseCase,
    getWatchedFilmsIds: GetWatchedFilmsIds
) : ViewModel() {

    var filmsList: Flow<PagingData<Film>>

    private val _newSearchConfig = MutableStateFlow(defaultSearchConfig)
    val newSearchConfig = _newSearchConfig.asStateFlow()

    private val searchConfig = MutableStateFlow(defaultSearchConfig)

    private val secondaryExceptionHandler = CoroutineExceptionHandler { _, _ ->
        viewModelScope.launch {
            _error.send(true)
        }
    }

    private val _error = Channel<Boolean>()
    val error = _error.receiveAsFlow()

    private val watchedFilmsIds =
        getWatchedFilmsIds.execute().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _countriesList = MutableStateFlow(emptyList<IsCheckedCoverUI<Country>>())
    val countriesList = combine(newSearchConfig, _countriesList) { searchConfig, countries ->
        countries.forEach {
            it.isChecked = it.item.id == searchConfig.country?.id
        }
        countries
    }.stateIn(viewModelScope, SharingStarted.Eagerly, _countriesList.value)

    private val _genresList = MutableStateFlow(emptyList<IsCheckedCoverUI<Genre>>())
    val genresList = combine(newSearchConfig, _genresList) { searchConfig, genres ->
        genres.forEach {
            it.isChecked = it.item.id == searchConfig.genre?.id
        }
        genres
    }.stateIn(viewModelScope, SharingStarted.Eagerly, _genresList.value)


    init {
        val originFilmsList = searchConfig.flatMapLatest {
            searchPagingFilmsUseCase.execute(it)
        }.cachedIn(viewModelScope)
        filmsList = combine(
            originFilmsList,
            watchedFilmsIds,
            searchConfig
        ) { films, watchedIds, searchConfig ->
            val newPagingFilms = films.map { film ->
                film.isWatched = film.kinopoiskId in watchedIds
                return@map film
            }
            if (!searchConfig.isShowingWatched)
                return@combine newPagingFilms.filter { !it.isWatched }
            else
                return@combine newPagingFilms
        }

    }


    fun getCountriesGenresLists(){
        viewModelScope.launch(secondaryExceptionHandler) {
            val lists = getFilmsCountriesGenresListUseCase.execute()
            _countriesList.value =
                lists.countries.filter { it.country.isNotBlank() }
                    .map { IsCheckedCoverUI(it, false) }
            _genresList.value =
                lists.genres.filter { it.genre.isNotBlank() }.map { IsCheckedCoverUI(it, false) }
        }
    }

    fun setSearchText(text: String?) {
        viewModelScope.launch {
            searchConfig.value = searchConfig.value.copy(keyword = text)
        }
    }

    fun setCountry(country: Country?) {
        viewModelScope.launch {
            _newSearchConfig.value = _newSearchConfig.value.copy(country = country)
        }
    }

    fun setGenre(genre: Genre?) {
        viewModelScope.launch {
            _newSearchConfig.value = _newSearchConfig.value.copy(genre = genre)
        }
    }

    fun setOrder(order: Order) {
        viewModelScope.launch {
            _newSearchConfig.value = _newSearchConfig.value.copy(order = order.keyword)
        }
    }

    fun setType(type: Type) {
        viewModelScope.launch {
            _newSearchConfig.value = _newSearchConfig.value.copy(type = type.keyword)
        }
    }

    fun setRatingRange(from: Int, to: Int) {
        viewModelScope.launch {
            _newSearchConfig.value = _newSearchConfig.value.copy(ratingFrom = from, ratingTo = to)
        }
    }

    fun setPeriodRange(yearFrom: Int, yearTo: Int) {
        viewModelScope.launch {
            _newSearchConfig.value =
                _newSearchConfig.value.copy(yearFrom = yearFrom, yearTo = yearTo)
        }
    }

    fun changeIsShowingWatched() {
        _newSearchConfig.value = _newSearchConfig.value.copy(
            isShowingWatched = !_newSearchConfig.value.isShowingWatched
        )
    }

    fun refreshNewSearchConfig() {
        _newSearchConfig.value = searchConfig.value
    }

    fun applyNewSearchConfig() {
        searchConfig.value = newSearchConfig.value
    }

    companion object {
        val defaultSearchConfig = SearchConfig(
            null, null, Order.RATING.keyword, Type.ALL.keyword, 0, 10, 1000, 3000, null, true
        )
    }

    enum class Order(val keyword: String) {
        RATING("RATING"),
        POPULARITY("NUM_VOTE"),
        DATE("YEAR")
    }

    enum class Type(val keyword: String) {
        ALL("ALL"),
        FILM("FILM"),
        TV_SERIES("TV_SERIES")
    }
}