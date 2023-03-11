package com.example.skillcinema.presentation.home_fragment

import android.animation.Animator
import android.animation.AnimatorInflater
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.skillcinema.R
import com.example.skillcinema.databinding.FragmentHomeBinding
import com.example.skillcinema.domain.model.Film
import com.example.skillcinema.domain.model.FilmsListType
import com.example.skillcinema.dpToPixel
import com.example.skillcinema.presentation.ErrorDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var animation: Animator

    private lateinit var mainAdapter: HomeFragmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        animation = AnimatorInflater.loadAnimator(requireContext(), R.animator.loading_anim)
        animation.setTarget(binding.progress)
        mainAdapter =
            HomeFragmentAdapter(onMoreBtnClick = { onMoreBtnClick(it) },
                onFilmClick = { onFilmClick(it) }, context = requireContext()
            )
        binding.errorBtn.setOnClickListener { viewModel.getContent() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.collectionsList.adapter = mainAdapter
        binding.collectionsList.addItemDecoration(
            getDivider(
                requireContext().dpToPixel(36f).toInt()
            )
        )
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.state.collect { state ->
                binding.progress.isVisible =
                    state.state == HomeViewModel.State.LOADING
                if (state.state == HomeViewModel.State.LOADING) animation.start()
                else animation.cancel()
                binding.collectionsList.isVisible =
                    state.state == HomeViewModel.State.SUCCESS
                binding.errorBtn.isVisible = state.state == HomeViewModel.State.ERROR
                mainAdapter.submitList(state.filmsLists)

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


    private fun onFilmClick(film: Film) {
        val direction = HomeFragmentDirections.actionHomeFragmentToInnerNavContainerFragment(film.kinopoiskId, null)
        findNavController().navigate(direction)
    }

    private fun onMoreBtnClick(collectionType: FilmsListType) {
        val direction = HomeFragmentDirections.actionHomeFragmentToInnerNavContainerFragment(0, collectionType)
        findNavController().navigate(direction)
    }

    private fun getDivider(space: Int): ItemDecoration {
        return object : ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val position = parent.getChildAdapterPosition(view)
                    .let { if (it == RecyclerView.NO_POSITION) return else it }
                outRect.bottom = space
                if (position == 0) outRect.top = space
            }
        }
    }


}