package com.example.skillcinema.presentation.onboarding_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.skillcinema.R
import com.example.skillcinema.databinding.FragmentOnboardingBinding


class OnBoardingFragment: Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!
    private val slidesList = listOf(
        SlideData(R.drawable.on_boarding_1, R.string.onboarding_1),
        SlideData(R.drawable.on_boarding_2, R.string.onboarding_2),
        SlideData(R.drawable.on_boarding_3, R.string.onboarding_3)
    )
    private val sliderAdapter = OnBoardingSliderAdapter(slidesList)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.slider.adapter = sliderAdapter
        setupDotsIndicator()
        binding.slider.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    setCurrentActiveDot(position)
                }
            }
        )
        binding.skipBtn.setOnClickListener {
            findNavController().navigate(R.id.action_onBoardingFragment_to_tabsFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupDotsIndicator() {
        repeat(slidesList.size) {
            val dot = ImageView(context).apply {
                setImageResource(R.drawable.indicator_inactive_dot)
            }
            binding.dotsLine.addView(dot)
        }
    }

    private fun setCurrentActiveDot(position: Int) {
        slidesList.forEachIndexed { index, _ ->
            val dot = binding.dotsLine.getChildAt(index) as ImageView
            if (index == position)
                dot.setImageResource(R.drawable.indicator_active_dot)
            else
                dot.setImageResource(R.drawable.indicator_inactive_dot)
        }

    }
}