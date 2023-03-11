package com.example.skillcinema.presentation.search_settings_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.skillcinema.IsCheckedCoverUI
import com.example.skillcinema.R
import com.example.skillcinema.databinding.FragmentGenreCountryListBinding
import com.example.skillcinema.domain.model.Country
import com.example.skillcinema.domain.model.Genre
import com.example.skillcinema.presentation.ErrorDialogFragment
import com.example.skillcinema.presentation.search_fragment.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
@OptIn(ExperimentalCoroutinesApi::class)
class GenreCountryListFragment : Fragment() {


    private var _binding: FragmentGenreCountryListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: GenreCountryListAdapter
    private val args: GenreCountryListFragmentArgs by navArgs()

    private var countryList = emptyList<IsCheckedCoverUI<Country>>()
    private var genreList = emptyList<IsCheckedCoverUI<Genre>>()


    private val viewModel: SearchViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getCountriesGenresLists()
        adapter = GenreCountryListAdapter(
            args.isCountryList,
            onCountryClick = { onCountryClick(it) },
            onGenreClick = { onGenreClick(it) },
            isListEmpty = { isListEmpty(it) },
            requireContext()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGenreCountryListBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationIcon(R.drawable.icon_caret_left)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        if (args.isCountryList) {
            binding.toolbarName.text = getString(R.string.country)
            binding.searchText.hint = getString(R.string.enter_country)
        } else {
            binding.toolbarName.text = getString(R.string.genre)
            binding.searchText.hint = getString(R.string.enter_genre)
        }

        binding.recyclerView.adapter = adapter
        val itemDecoration = DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(
            ResourcesCompat.getDrawable(resources, R.drawable.list_divider, null)!!
        )
        binding.recyclerView.addItemDecoration(itemDecoration)

        binding.searchText.doOnTextChanged { text, _, _, _ ->
            if (args.isCountryList) {
                text?.let {
                    adapter.setCountryList(countryList.filter {
                        it.item.country.contains(
                            text,
                            true
                        )
                    }
                        .sortedByDescending { it.item.country.startsWith(text, true) })
                } ?: adapter.setCountryList(countryList)
            } else {
                text?.let {
                    adapter.setGenreList(genreList.filter { it.item.genre.contains(text, true) }
                        .sortedByDescending { it.item.genre.startsWith(text, true) })
                } ?: adapter.setGenreList(genreList)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (args.isCountryList) {
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.countriesList.collect {
                    countryList = it
                    adapter.setCountryList(countryList)
                }
            }
        } else {
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.genresList.collect {
                    genreList = it
                    adapter.setGenreList(genreList)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.error.collect {
                if (it) ErrorDialogFragment().show(parentFragmentManager, "tag")
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onCountryClick(country: IsCheckedCoverUI<Country>) {
        if (country.isChecked) viewModel.setCountry(null)
        else viewModel.setCountry(country.item)
        findNavController().popBackStack()
    }

    private fun onGenreClick(genre: IsCheckedCoverUI<Genre>) {
        if (genre.isChecked) viewModel.setGenre(null)
        else viewModel.setGenre(genre.item)
        findNavController().popBackStack()
    }

    private fun isListEmpty(isEmpty: Boolean) {
        binding.recyclerView.isVisible = !isEmpty
        binding.searchFailedText.isVisible = isEmpty
    }

}