package com.example.skillcinema.presentation.actor_info_fragment

import android.animation.Animator
import android.animation.AnimatorInflater
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.skillcinema.R
import com.example.skillcinema.databinding.FragmentActorInfoBinding
import com.example.skillcinema.domain.model.Film
import com.example.skillcinema.domain.model.FilmsListType
import com.example.skillcinema.presentation.ErrorDialogFragment
import com.example.skillcinema.presentation.adapters.BasicHorizontalLinearDivider
import com.example.skillcinema.presentation.adapters.FilmListAdapterWithFooterMore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActorInfoFragment : Fragment() {

    private var _binding: FragmentActorInfoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ActorInfoViewModel by viewModels()
    private val args: ActorInfoFragmentArgs by navArgs()
    private var filmListType: FilmsListType? = null
    private lateinit var animation: Animator

    private val filmListAdapter = FilmListAdapterWithFooterMore(
        onFilmClick = { onFilmClick(it) },
        onFooterBtnClick = { onMoreBtnClick() },
        loadFilmPoster = { loadFilmPoster(it) },
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getPersonInfo(args.stuffId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActorInfoBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationIcon(R.drawable.icon_caret_left)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        animation = AnimatorInflater.loadAnimator(requireContext(), R.animator.loading_anim)
        animation.setTarget(binding.progress)
        with(binding.bestFilms) {
            collectionName.text = getString(R.string.best_films)
            getAllIcon.visibility = View.VISIBLE
            recyclerView.addItemDecoration(BasicHorizontalLinearDivider(requireContext()))
            recyclerView.adapter = filmListAdapter
            getAllBtn.setOnClickListener {
                onMoreBtnClick()
            }
        }
        with(binding.filmography) {
            sectionName.text = getString(R.string.filmography)
            getAllIcon.visibility = View.VISIBLE
            allOrNumber.text = getString(R.string.to_the_list)
            getAllBtn.setOnClickListener {
                val direction =
                    ActorInfoFragmentDirections.actionActorInfoFragmentToFilmographyFragment(args.stuffId)
                findNavController().navigate(direction)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.personInfo.collect { personInfo ->
                filmListType = FilmsListType.PersonsBest(personInfo.name, personInfo.personId)
                Glide.with(requireContext())
                    .load(personInfo.posterUrl)
                    .into(binding.personImg)
                binding.personName.text = personInfo.name
                binding.personProfessions.text = personInfo.profession
                val filmsAmount = personInfo.films.size
                binding.filmography.infoText.text = resources.getQuantityString(
                    R.plurals.films_count,
                    filmsAmount,
                    filmsAmount
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.bestFilms.collect {
                binding.bestFilms.getAllBtn.isVisible = it.size >= 20
                filmListAdapter.submitList(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.error.collect {
                if (it) ErrorDialogFragment().show(parentFragmentManager, "tag")
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.state.collect { state ->
                binding.progress.isVisible =
                    state == ActorInfoViewModel.State.LOADING
                if (state == ActorInfoViewModel.State.LOADING) animation.start()
                else animation.cancel()
                binding.scrollView.isVisible = state == ActorInfoViewModel.State.SUCCESS
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onFilmClick(film: Film) {
        val direction = ActorInfoFragmentDirections.actionToFilmInfoFragment(film.kinopoiskId)
        findNavController().navigate(direction)
    }

    private fun onMoreBtnClick() {
        filmListType?.let {
            val direction = ActorInfoFragmentDirections.actionToListFragment(filmListType!!)
            findNavController().navigate(direction)
        }
    }

    private fun loadFilmPoster(film: Film) {
        viewModel.loadFilmPoster(film.kinopoiskId)
    }
}