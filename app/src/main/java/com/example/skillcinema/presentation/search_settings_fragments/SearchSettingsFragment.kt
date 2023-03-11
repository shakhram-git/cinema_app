package com.example.skillcinema.presentation.search_settings_fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.skillcinema.R
import com.example.skillcinema.capitalize
import com.example.skillcinema.databinding.FragmentSearchSettingsBinding
import com.example.skillcinema.presentation.search_fragment.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class SearchSettingsFragment : Fragment() {


    private val viewModel: SearchViewModel by activityViewModels()
    private var _binding: FragmentSearchSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel.refreshNewSearchConfig()
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchSettingsBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationIcon(R.drawable.icon_caret_left)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.applyButton.setOnClickListener {
            viewModel.applyNewSearchConfig()
            findNavController().popBackStack()
        }

        binding.countryContainer.setOnClickListener {
            val direction = SearchSettingsFragmentDirections
                .actionSearchSettingsFragmentToGenreCountryListFragment(true)
            findNavController().navigate(direction)
        }
        binding.genreContainer.setOnClickListener {
            val direction = SearchSettingsFragmentDirections
                .actionSearchSettingsFragmentToGenreCountryListFragment(false)
            findNavController().navigate(direction)
        }
        binding.yearPeriodContainer.setOnClickListener {
            findNavController().navigate(R.id.action_searchSettingsFragment_to_searchPeriodFragment)
        }

        SearchViewModel.Type.values().forEachIndexed { index, type ->
            val radioButton = layoutInflater.inflate(
                R.layout.item_radio_button, binding.typesGroup, false
            ) as RadioButton
            radioButton.tag = type
            radioButton.id = index
            radioButton.text = when (type) {
                SearchViewModel.Type.ALL -> getString(R.string.all)
                SearchViewModel.Type.FILM -> getString(R.string.films)
                SearchViewModel.Type.TV_SERIES -> getString(R.string.series)
            }
            radioButton.background = getButtonBackground(index)
            radioButton.setOnClickListener { viewModel.setType(radioButton.tag as SearchViewModel.Type) }
            binding.typesGroup.addView(radioButton)
        }

        SearchViewModel.Order.values().forEachIndexed { index, order ->
            val radioButton = layoutInflater.inflate(
                R.layout.item_radio_button, binding.typesGroup, false
            ) as RadioButton
            radioButton.tag = order
            radioButton.id = index
            radioButton.text = when (order) {
                SearchViewModel.Order.RATING -> getString(R.string.rating)
                SearchViewModel.Order.POPULARITY -> getString(R.string.popularity)
                SearchViewModel.Order.DATE -> getString(R.string.date)
            }
            radioButton.background = getButtonBackground(index)
            radioButton.setOnClickListener { viewModel.setOrder(radioButton.tag as SearchViewModel.Order) }
            binding.orderGroup.addView(radioButton)
        }
        binding.ratingSlider.addOnChangeListener { slider, _, fromUser ->
            val values = slider.values
            if (fromUser) {
                if (values[0].toInt() == 1 && values[1].toInt() == 10)
                    viewModel.setRatingRange(0, 10)
                else viewModel.setRatingRange(values[0].toInt(), values[1].toInt())
            }
        }
        binding.isShowedContainer.setOnClickListener {
            viewModel.changeIsShowingWatched()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.newSearchConfig.collect { searchConfig ->
                val typeIndex =
                    SearchViewModel.Type.values().indexOfFirst { it.keyword == searchConfig.type }
                binding.typesGroup.check(typeIndex)

                val orderIndex =
                    SearchViewModel.Order.values().indexOfFirst { it.keyword == searchConfig.order }
                binding.orderGroup.check(orderIndex)

                binding.countryName.text = searchConfig.country?.country ?: "-"
                binding.genreName.text = searchConfig.genre?.genre?.capitalize() ?: "-"
                binding.yearFromTo.text =
                    if (searchConfig.yearFrom == SearchViewModel.defaultSearchConfig.yearFrom
                        && searchConfig.yearTo == SearchViewModel.defaultSearchConfig.yearTo
                    ) {
                        getString(R.string.any)
                    } else {
                        if (searchConfig.yearFrom == searchConfig.yearTo){
                            searchConfig.yearFrom.toString()
                        } else {
                            var text = ""
                            if (searchConfig.yearFrom != SearchViewModel.defaultSearchConfig.yearFrom)
                                text = getString(R.string.number_from, searchConfig.yearFrom)
                            if (searchConfig.yearTo != SearchViewModel.defaultSearchConfig.yearTo)
                                text += " ${getString(R.string.number_till, searchConfig.yearTo)}"
                            text
                        }
                    }
                binding.ratingText.text =
                    if (searchConfig.ratingFrom == SearchViewModel.defaultSearchConfig.ratingFrom
                        && searchConfig.ratingTo == SearchViewModel.defaultSearchConfig.ratingTo
                    ) {
                        getString(R.string.any)
                    } else {
                        "${getString(R.string.number_from, searchConfig.ratingFrom)} " +
                                getString(R.string.number_till, searchConfig.ratingTo)
                    }
                if (searchConfig.isShowingWatched) {
                    binding.isHidingImg.setImageResource(R.drawable.icon_eye)
                    binding.isHidingImg.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.light_blue
                        )
                    )
                    binding.isHidingText.text = getString(R.string.dont_hide_watched)

                } else {
                    binding.isHidingImg.setImageResource(R.drawable.icon_eye_crossed)
                    binding.isHidingImg.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.black
                        )
                    )
                    binding.isHidingText.text = getString(R.string.hide_watched)
                }
                binding.ratingSlider.values = listOf(
                    if (searchConfig.ratingFrom == 0) 1f else searchConfig.ratingFrom.toFloat(),
                    searchConfig.ratingTo.toFloat()
                )
                binding.ratingText.text =
                    if (searchConfig.ratingFrom == 0 && searchConfig.ratingTo == 10)
                        getString(R.string.any) else "${
                        getString(
                            R.string.number_from,
                            searchConfig.ratingFrom
                        )
                    } ${getString(R.string.number_till, searchConfig.ratingTo)}"
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getButtonBackground(index: Int): Drawable? {
        return when (index) {
            0 -> ResourcesCompat.getDrawable(
                resources, R.drawable.background_radiobtn_left, null
            )
            SearchViewModel.Type.values().size - 1 -> ResourcesCompat.getDrawable(
                resources, R.drawable.background_radiobtn_right, null
            )
            else -> ResourcesCompat.getDrawable(
                resources, R.drawable.background_radiobtn_center, null
            )
        }
    }

}