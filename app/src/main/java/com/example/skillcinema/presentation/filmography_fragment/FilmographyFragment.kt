package com.example.skillcinema.presentation.filmography_fragment

import android.animation.Animator
import android.animation.AnimatorInflater
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.skillcinema.R
import com.example.skillcinema.databinding.FragmentFilmographyBinding
import com.example.skillcinema.domain.model.Film
import com.example.skillcinema.presentation.ErrorDialogFragment
import com.example.skillcinema.presentation.adapters.BasicVerticalLinearDivider
import com.example.skillcinema.presentation.adapters.FilmListAdapter2
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilmographyFragment : Fragment() {

    private var _binding: FragmentFilmographyBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FilmographyViewModel by viewModels()
    private lateinit var animation: Animator
    private val args: FilmographyFragmentArgs by navArgs()

    private val filmListAdapter = FilmListAdapter2(
        onFilmClick = { onFilmClick(it) },
        loadFilmPoster = { loadFilmPoster(it) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getPersonInfo(args.staffId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilmographyBinding.inflate(inflater, container, false)
        binding.recyclerView.adapter = filmListAdapter
        binding.recyclerView.addItemDecoration(BasicVerticalLinearDivider(requireContext()))
        binding.toolbar.setNavigationIcon(R.drawable.icon_caret_left)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        animation = AnimatorInflater.loadAnimator(requireContext(), R.animator.loading_anim)
        animation.setTarget(binding.progress)
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.professionsList.collect { professions ->
                var firstChipId = 0
                professions.forEachIndexed { index, prof ->
                    val chip =
                        layoutInflater.inflate(R.layout.item_chip, binding.chipGroup, false) as Chip
                    if (index == 0) {
                        firstChipId = View.generateViewId()
                        chip.id = firstChipId
                    }
                    chip.tag = prof
                    chip.text = when (prof) {
                        FilmographyViewModel.Profession.ACTOR -> {
                            if (prof.isFemale) getString(R.string.actress) else getString(R.string.actor)
                        }
                        FilmographyViewModel.Profession.WRITER -> getString(R.string.writer)
                        FilmographyViewModel.Profession.SELF_PLAYING_ACTOR -> {
                            if (prof.isFemale) getString(R.string.actress_playing_herself) else getString(
                                R.string.actor_playing_himself
                            )
                        }
                        FilmographyViewModel.Profession.IN_TITLES -> {
                            if (prof.isFemale) getString(R.string.actress_in_credits) else getString(R.string.actor_in_credits)
                        }
                        FilmographyViewModel.Profession.DIRECTOR -> getString(R.string.director)
                        FilmographyViewModel.Profession.PRODUCER -> getString(R.string.producer)
                        FilmographyViewModel.Profession.OPERATOR -> getString(R.string.operator)
                        FilmographyViewModel.Profession.COMPOSER -> getString(R.string.composer)
                    }
                    val amountText = SpannableString(prof.filmAmount.toString())
                    amountText.setSpan(
                        AbsoluteSizeSpan(resources.getDimensionPixelOffset(R.dimen.chip_amount_text_size)),
                        0,
                        prof.filmAmount.toString().length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    amountText.setSpan(
                        ForegroundColorSpan(
                            resources.getColor(
                                R.color.light_grey,
                                null
                            )
                        ), 0, prof.filmAmount.toString().length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    chip.append("   ")
                    chip.append(amountText)
                    chip.setOnCheckedChangeListener { button, isChecked ->
                        if (isChecked) viewModel.filterByProf(button.tag as FilmographyViewModel.Profession)
                    }
                    binding.chipGroup.addView(chip)
                }
                binding.chipGroup.check(firstChipId)

            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.personInfo.collect { personInfo ->
                binding.personName.text = personInfo.name

            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.filmsList.collect {
                filmListAdapter.submitList(it)
                filmListAdapter.notifyDataSetChanged()
                binding.recyclerView.scrollToPosition(0)
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.error.collect {
                if (it) ErrorDialogFragment().show(parentFragmentManager, "tag")
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.state.collect { state ->
                if (state == FilmographyViewModel.State.LOADING) animation.start()
                else animation.cancel()
                binding.progress.isVisible = state == FilmographyViewModel.State.LOADING
                binding.content.isVisible = state == FilmographyViewModel.State.SUCCESS
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun onFilmClick(film: Film) {
        val direction = FilmographyFragmentDirections.actionToFilmInfoFragment(film.kinopoiskId)
        findNavController().navigate(direction)
    }

    private fun loadFilmPoster(film: Film) {
        viewModel.loadFilmPoster(film.kinopoiskId)
    }
}