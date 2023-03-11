package com.example.skillcinema.presentation.search_settings_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.skillcinema.R
import com.example.skillcinema.databinding.CustomDatePickerBinding
import com.example.skillcinema.databinding.FragmentSearchPeriodBinding
import com.example.skillcinema.presentation.search_fragment.SearchViewModel
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class SearchPeriodFragment : Fragment() {

    private var _binding: FragmentSearchPeriodBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by activityViewModels()

    private var yearPeriodsList = emptyList<List<Int>>()
    private var yearFrom = 0
    private var yearTo = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        yearPeriodsList = getYearPeriodsList()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchPeriodBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationIcon(R.drawable.icon_caret_left)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.applyButton.setOnClickListener {
            viewModel.setPeriodRange(yearFrom, yearTo)
            findNavController().popBackStack()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.newSearchConfig.collect { searchConfig ->
                yearFrom = searchConfig.yearFrom
                yearTo = searchConfig.yearTo
                setDataPicker(binding.datePickerFrom, getYearsPeriodByYear(yearFrom), true)
                setDataPicker(binding.datePickerTo, getYearsPeriodByYear(yearTo), false)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun getYearPeriodsList(): List<List<Int>> {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val yearsList = (START_YEAR..currentYear + 1).toList()
        val yearPeriodsList = buildList {
            val periodsAmount = if (yearsList.size % PERIOD_SIZE != 0)
                (yearsList.size / PERIOD_SIZE) + 1
            else yearsList.size / PERIOD_SIZE
            for (i in 1..periodsAmount) {
                val years = yearsList.dropLast((i - 1) * PERIOD_SIZE).takeLast(PERIOD_SIZE)
                add(years)
            }
        }
        return yearPeriodsList.reversed()
    }


    private fun getYearsPeriodByYear(year: Int): List<Int> {
        return yearPeriodsList.firstOrNull { years -> years.any { it == year } }
            ?: yearPeriodsList.last()
    }

    private fun setDataPicker(
        datePicker: CustomDatePickerBinding,
        yearsList: List<Int>,
        isFrom: Boolean
    ) {
        setDataPickerHeader(datePicker, yearsList, isFrom)
        datePicker.dateContainer.removeAllViews()
        yearsList.forEach { year ->
            val chip = layoutInflater.inflate(R.layout.item_chip_date, binding.root, false) as Chip
            chip.text = year.toString()
            chip.id = year
            chip.isCheckable = if (isFrom) chip.id <= yearTo else chip.id >= yearFrom
            chip.setOnCheckedChangeListener { compoundButton, isChecked ->
                if (isChecked) {
                    if (isFrom) yearFrom = compoundButton.id else yearTo = compoundButton.id
                } else {
                    if (isFrom) yearFrom =
                        SearchViewModel.defaultSearchConfig.yearFrom else yearTo =
                        SearchViewModel.defaultSearchConfig.yearTo
                }
                if (isFrom) {
                    refreshDatePicker(binding.datePickerTo, false)
                } else {
                    refreshDatePicker(binding.datePickerFrom, true)
                }
            }
            datePicker.dateContainer.addView(chip)
        }

        datePicker.dateContainer.check(if (isFrom) yearFrom else yearTo)
    }

    private fun setDataPickerHeader(
        datePicker: CustomDatePickerBinding,
        yearsList: List<Int>,
        isFrom: Boolean
    ) {
        datePicker.textPeriod.text = "${yearsList.first()} - ${yearsList.last()}"
        datePicker.forwardBtn.isClickable =
            yearPeriodsList.indexOf(yearsList) != yearPeriodsList.lastIndex
        if (yearPeriodsList.indexOf(yearsList) != yearPeriodsList.lastIndex) {
            datePicker.forwardBtn.setOnClickListener {
                val yearPeriod = yearPeriodsList[yearPeriodsList.indexOf(yearsList) + 1]

                setDataPicker(
                    datePicker,
                    yearPeriod,
                    isFrom
                )
            }
            datePicker.forwardBtn.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.dark_grey
                )
            )
        } else {
            datePicker.forwardBtn.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.mid_grey
                )
            )
        }
        datePicker.backBtn.isClickable = yearPeriodsList.indexOf(yearsList) != 0
        if (yearPeriodsList.indexOf(yearsList) != 0) {
            datePicker.backBtn.setOnClickListener {
                val yearPeriod = yearPeriodsList[yearPeriodsList.indexOf(yearsList) - 1]
                setDataPicker(
                    datePicker,
                    yearPeriod,
                    isFrom
                )
            }
            datePicker.backBtn.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.dark_grey
                )
            )
        } else {
            datePicker.backBtn.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.mid_grey
                )
            )
        }

    }

    private fun refreshDatePicker(datePicker: CustomDatePickerBinding, isFrom: Boolean) {
        val chips = datePicker.dateContainer.children.toList()
        datePicker.dateContainer.removeAllViews()
        chips.forEach { view ->
            val chip = view as Chip
            chip.isCheckable = if (isFrom) chip.id <= yearTo else chip.id >= yearFrom
            datePicker.dateContainer.addView(chip)
        }

    }


    companion object {
        const val START_YEAR = 1895
        const val PERIOD_SIZE = 12
    }

}