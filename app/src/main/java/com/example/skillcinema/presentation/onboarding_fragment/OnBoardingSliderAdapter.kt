package com.example.skillcinema.presentation.onboarding_fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.databinding.OnboardingSlideBinding

class OnBoardingSliderAdapter(private val slidesList: List<SlideData>) :
    RecyclerView.Adapter<OnBoardingSliderAdapter.OnBoardingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingViewHolder {
        return OnBoardingViewHolder(
            OnboardingSlideBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: OnBoardingViewHolder, position: Int) {
        with(holder.binding) {
            img.setImageResource(slidesList[position].imageSrc)
            text.setText(slidesList[position].textSrc)
        }
    }

    override fun getItemCount(): Int = slidesList.size

    class OnBoardingViewHolder(val binding: OnboardingSlideBinding) :
        RecyclerView.ViewHolder(binding.root)
}