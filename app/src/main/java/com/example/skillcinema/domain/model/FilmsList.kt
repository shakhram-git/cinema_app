package com.example.skillcinema.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class FilmsList(
    val type: FilmsListType,
    val films: List<Film>
)

@Parcelize
sealed class ListType(open val name: String, open val isPaging: Boolean) : Parcelable

@Parcelize
sealed class FilmsListType(override val name: String, override val isPaging: Boolean) :
    ListType(name, isPaging), Parcelable {

    @Parcelize
    object Premieres : FilmsListType("Премьеры", false), Parcelable

    @Parcelize
    object Popular : FilmsListType("Популярные", true), Parcelable

    @Parcelize
    object Top250 : FilmsListType("Топ-250", true), Parcelable

    @Parcelize
    object Series : FilmsListType("Сериалы", true), Parcelable

    @Parcelize
    data class Filtered(override val name: String, val countryID: Long, val genreId: Long) :
        FilmsListType(name, true),
        Parcelable

    @Parcelize
    data class Similar(val filmName: String, val filmId: Long) :
        FilmsListType("Похожие фильмы: $filmName", false), Parcelable

    @Parcelize
    data class PersonsBest(val personName: String, val personId: Long) :
        FilmsListType("Лучшее: $personName", false),
        Parcelable

    @Parcelize
    data class LocalCollectionFilms(val collectionName: String, val collectionId: Int) :
        FilmsListType(collectionName, false),
        Parcelable
}

@Parcelize
sealed class StaffListType(override val name: String, open val filmId: Long) :
    ListType(name, false),
    Parcelable {
    @Parcelize
    data class Actors(override val filmId: Long) : StaffListType("В фильме снимались", filmId),
        Parcelable

    @Parcelize
    data class OtherStaff(override val filmId: Long) :
        StaffListType("Над фильмом работали", filmId),
        Parcelable
}