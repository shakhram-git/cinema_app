package com.example.skillcinema.data.remote.retrofit.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FilmCountriesGenresListDto (
    val genres: List<GenreDto>,
    val countries: List<CountryDto>
){
    @JsonClass(generateAdapter = true)
    data class CountryDto (
        val id: Long,
        val country: String
    )

    @JsonClass(generateAdapter = true)
    data class GenreDto (
        val id: Long,
        val genre: String
    )
}
