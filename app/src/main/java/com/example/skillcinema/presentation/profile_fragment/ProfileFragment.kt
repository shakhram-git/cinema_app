package com.example.skillcinema.presentation.profile_fragment

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
import com.example.skillcinema.DefaultCollections
import com.example.skillcinema.R
import com.example.skillcinema.databinding.FragmentProfileBinding
import com.example.skillcinema.domain.model.Film
import com.example.skillcinema.domain.model.FilmsListType
import com.example.skillcinema.dpToPixel
import com.example.skillcinema.presentation.ErrorDialogFragment
import com.example.skillcinema.presentation.adapters.BasicHorizontalLinearDivider
import com.example.skillcinema.presentation.adapters.FilmListAdapterWithFooterWipe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    private lateinit var animation: Animator

    private val watchedFilmsAdapter = FilmListAdapterWithFooterWipe(
        onFilmClick = { onFilmClick(it) },
        onFooterBtnClick = { onWipeDataBtnClick(DefaultCollections.WATCHED) }
    )
    private val interestedFilmsAdapter = FilmListAdapterWithFooterWipe(
        onFilmClick = { onFilmClick(it) },
        onFooterBtnClick = { onWipeDataBtnClick(DefaultCollections.INTERESTED) }
    )
    private val collectionsAdapter = ProfileCollectionsAdapter(
        onCollectionClick = { openCollection(it.name, it.id) },
        onDeleteBtnClick = { deleteCollection(it.id) }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.createCollectionBtn.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToNewCollectionDialogFragment2())
        }
        binding.watchedList.recyclerView.adapter = watchedFilmsAdapter
        binding.interestedList.recyclerView.adapter = interestedFilmsAdapter
        binding.watchedList.recyclerView.addItemDecoration(
            BasicHorizontalLinearDivider(
                requireContext()
            )
        )
        binding.interestedList.recyclerView.addItemDecoration(
            BasicHorizontalLinearDivider(
                requireContext()
            )
        )
        binding.collectionsContainer.adapter = collectionsAdapter
        binding.collectionsContainer.addItemDecoration(getGridLayoutDivider())
        animation = AnimatorInflater.loadAnimator(requireContext(), R.animator.loading_anim)
        animation.setTarget(binding.progress)
        binding.errorBtn.setOnClickListener { viewModel.getContent() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.watchedFilms.collect { collection ->
                watchedFilmsAdapter.submitList(collection.films.take(20))
                watchedFilmsAdapter.setIsListEmpty(collection.films.isEmpty())
                with(binding.watchedList) {
                    getAllIcon.isVisible = true
                    collectionName.text = collection.name
                    allOrNumber.text = collection.films.size.toString()
                    getAllBtn.isVisible = collection.films.isNotEmpty()
                    getAllBtn.setOnClickListener {
                        openCollection(collection.name, collection.id)
                    }
                    recyclerView.scrollToPosition(0)
                }

            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.interestedFilms.collect { collection ->
                interestedFilmsAdapter.submitList(collection.films.take(20))
                interestedFilmsAdapter.setIsListEmpty(collection.films.isEmpty())
                with(binding.interestedList) {
                    getAllIcon.isVisible = true
                    collectionName.text = collection.name
                    allOrNumber.text = collection.films.size.toString()
                    getAllBtn.isVisible = collection.films.isNotEmpty()
                    getAllBtn.setOnClickListener {
                        openCollection(collection.name, collection.id)
                    }
                    recyclerView.scrollToPosition(0)
                }

            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.collections.collect {
                collectionsAdapter.submitList(it)
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.error.collect {
                if (it) ErrorDialogFragment().show(parentFragmentManager, "tag")
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.state.collect { state ->
                binding.progress.isVisible = state == ProfileViewModel.State.LOADING
                binding.content.isVisible = state == ProfileViewModel.State.SUCCESS
                if (state == ProfileViewModel.State.LOADING) animation.start() else animation.cancel()
                binding.errorBtn.isVisible = state == ProfileViewModel.State.ERROR
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onFilmClick(film: Film) {
        val direction = ProfileFragmentDirections.actionProfileFragmentToInnerNavContainerFragment3(
            film.kinopoiskId,
            null
        )
        findNavController().navigate(direction)
    }

    private fun onWipeDataBtnClick(collectionId: Int) {
        viewModel.cleanCollection(collectionId)
    }

    private fun getGridLayoutDivider(): RecyclerView.ItemDecoration {
        val space = requireContext().dpToPixel(16f).toInt()
        return object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val position = parent.getChildAdapterPosition(view)
                outRect.top = if (position < 2) 0 else space
                if (position % 2 == 0) outRect.right = space
            }
        }
    }

    private fun openCollection(collectionName: String, collectionId: Int) {
        val direction = ProfileFragmentDirections.actionProfileFragmentToInnerNavContainerFragment3(
            0, FilmsListType.LocalCollectionFilms(collectionName, collectionId)
        )
        findNavController().navigate(direction)
    }

    private fun deleteCollection(collectionId: Int) {
        viewModel.deleteCollection(collectionId)
    }

}