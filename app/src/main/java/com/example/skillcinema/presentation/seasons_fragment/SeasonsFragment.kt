package com.example.skillcinema.presentation.seasons_fragment

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
import androidx.viewpager2.widget.ViewPager2
import com.example.skillcinema.R
import com.example.skillcinema.databinding.FragmentSeasonsBinding
import com.example.skillcinema.presentation.ErrorDialogFragment
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SeasonsFragment : Fragment() {


    private val viewModel: SeasonsViewModel by viewModels()
    private val args: SeasonsFragmentArgs by navArgs()

    private var _binding: FragmentSeasonsBinding? = null
    private val binding get() = _binding!!

    private lateinit var sliderAdapter: SeasonsSliderAdapter

    private lateinit var animation: Animator


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sliderAdapter = SeasonsSliderAdapter(requireContext())
        viewModel.getSeasons(args.filmId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSeasonsBinding.inflate(inflater, container, false)
        binding.filmName.text = args.filmName
        binding.toolbar.setNavigationIcon(R.drawable.icon_caret_left)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.slider.adapter = sliderAdapter
        animation = AnimatorInflater.loadAnimator(requireContext(), R.animator.loading_anim)
        animation.setTarget(binding.progress)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.seasons.collect { seasons ->
                sliderAdapter.setData(seasons.seasons)
                val firstChipId = 0
                seasons.seasons.forEachIndexed { index, season ->
                    val chip =
                        layoutInflater.inflate(
                            R.layout.item_chip,
                            binding.seasonsList,
                            false
                        ) as Chip
                    chip.id = index
                    chip.text = season.number.toString()
                    chip.tag = season.number
                    chip.setOnCheckedChangeListener { button, isChecked ->
                        if (isChecked) {
                            binding.slider.setCurrentItem(button.id, true)
                        }
                    }
                    binding.seasonsList.addView(chip)
                }
                binding.seasonsList.check(firstChipId)
                binding.slider.registerOnPageChangeCallback(
                    object : ViewPager2.OnPageChangeCallback() {
                        override fun onPageSelected(position: Int) {
                            super.onPageSelected(position)
                            binding.seasonsList.check(position)
                        }
                    }
                )

            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.error.collect {
                if (it) ErrorDialogFragment().show(parentFragmentManager, "tag")
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.state.collect { state ->
                binding.progress.isVisible = state == SeasonsViewModel.State.LOADING
                binding.content.isVisible = state == SeasonsViewModel.State.SUCCESS
                if (state == SeasonsViewModel.State.LOADING) animation.start()
                else animation.cancel()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}