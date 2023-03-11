package com.example.skillcinema.presentation.search_fragment

import android.animation.Animator
import android.animation.AnimatorInflater
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.skillcinema.R
import com.example.skillcinema.databinding.FragmentSearchBinding
import com.example.skillcinema.domain.model.Film
import com.example.skillcinema.presentation.ErrorDialogFragment
import com.example.skillcinema.presentation.adapters.BasicVerticalLinearDivider
import com.example.skillcinema.presentation.adapters.DefaultLoadStateAdapter
import com.example.skillcinema.presentation.adapters.PagingFilmListAdapter2
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val adapter =
        PagingFilmListAdapter2(onFilmClick = { onFilmClick(it) })
    private lateinit var footerAdapter: DefaultLoadStateAdapter

    private val viewModel: SearchViewModel by activityViewModels()
    private lateinit var animation: Animator


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        animation = AnimatorInflater.loadAnimator(requireContext(), R.animator.loading_anim)
        animation.setTarget(binding.progress)
        footerAdapter = DefaultLoadStateAdapter(requireContext())
        adapter.addLoadStateListener { state ->
            binding.recyclerView.isVisible = state.refresh != LoadState.Loading
            binding.progress.isVisible = state.refresh == LoadState.Loading
            if (state.refresh is LoadState.Error) {
                ErrorDialogFragment().show(parentFragmentManager, "tag")
            }
            if (state.refresh == LoadState.Loading) animation.start()
            else animation.cancel()
            binding.searchFailedText.isVisible = adapter.itemCount < 1
        }
        binding.recyclerView.adapter = adapter.withLoadStateFooter(footerAdapter)
        binding.recyclerView.addItemDecoration(BasicVerticalLinearDivider(requireContext()))
        binding.searchText.setOnEditorActionListener { textView, i, _ ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.setSearchText(textView.text.toString())
                hideKeyboardFrom(requireContext(), binding.searchText)
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener true
        }
        binding.searchSettingsBtn.setOnClickListener {
            Navigation.findNavController(requireActivity(), R.id.main_fragment_container)
                .navigate(R.id.action_tabsFragment_to_searchSettingsFragment)
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.filmsList.collect { adapter.submitData(it) }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onFilmClick(film: Film) {
        val direction = SearchFragmentDirections.actionHomeFragmentToInnerNavContainerFragment(
            film.kinopoiskId, null
        )
        findNavController().navigate(direction)
    }

    private fun hideKeyboardFrom(context: Context, view: View?) {
        val imm =
            context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

}