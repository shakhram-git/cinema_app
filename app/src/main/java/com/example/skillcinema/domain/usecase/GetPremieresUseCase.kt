package com.example.skillcinema.domain.usecase

import android.icu.util.Calendar
import com.example.skillcinema.domain.model.Film
import com.example.skillcinema.domain.model.FilmsList
import com.example.skillcinema.domain.model.FilmsListType
import com.example.skillcinema.domain.repository.FilmRepository
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class GetPremieresUseCase @Inject constructor(private val filmRepository: FilmRepository) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private lateinit var calendar: Calendar

    private val months = listOf(
        "JANUARY",
        "FEBRUARY",
        "MARCH",
        "APRIL",
        "MAY",
        "JUNE",
        "JULY",
        "AUGUST",
        "SEPTEMBER",
        "OCTOBER",
        "NOVEMBER",
        "DECEMBER"
    )

    suspend fun execute(): FilmsList {
        calendar = Calendar.getInstance()

        calendar.set(Calendar.HOUR, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        val startYear = calendar[Calendar.YEAR]
        val startMonth = calendar[Calendar.MONTH]
        val startTime = calendar.timeInMillis

        val currentWeekNumber = calendar.get(Calendar.WEEK_OF_YEAR)
        calendar.set(Calendar.WEEK_OF_YEAR, currentWeekNumber + 2)

        val endYear = calendar[Calendar.YEAR]
        val endMonth = calendar[Calendar.MONTH]
        val endTime = calendar.timeInMillis

        val premieresList =
            (filmRepository.getPremieresOfMonth(startYear, months[startMonth])).toMutableList()
        if (startMonth != endMonth) {
            val list = filmRepository.getPremieresOfMonth(endYear, months[endMonth])
            premieresList.addAll(list)
        }
        val premieresListFor2Weeks = filterPremieresList(premieresList.toList(), startTime, endTime)

        return FilmsList(FilmsListType.Premieres, premieresListFor2Weeks)
    }

    private fun filterPremieresList(
        list: List<Film>,
        startDay: Long,
        endDay: Long
    ): List<Film> {

        val result = list.filter { film ->
            val date = film.premiereRu?.let { dateFormat.parse(it) }
            /*calendar.time = film.premiereRu?.let { dateFormat.parse(it) } ?: */
            val premiereTime = date?.time
            premiereTime in startDay..endDay
        }
        return result
    }
}