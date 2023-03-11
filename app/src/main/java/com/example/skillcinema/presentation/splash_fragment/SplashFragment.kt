package com.example.skillcinema.presentation.splash_fragment

import android.animation.Animator
import android.animation.AnimatorInflater
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.skillcinema.R
import com.example.skillcinema.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private lateinit var animation: Animator
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        animation = AnimatorInflater.loadAnimator(requireContext(), R.animator.loading_anim)
        animation.setTarget(binding.progress)
        animation.start()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenStarted {
            viewModel.isFirstEnter.collect { isFirstStart ->
                isFirstStart?.let {
                    if (isFirstStart) {
                        Handler(Looper.getMainLooper()).postDelayed(
                            { findNavController().navigate(R.id.action_splashFragment_to_onBoardingFragment) },
                            1500
                        )

                    } else {
                        Handler(Looper.getMainLooper()).postDelayed(
                            { findNavController().navigate(R.id.action_splashFragment_to_tabsFragment) },
                            1500
                        )
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        animation.cancel()
        _binding = null

    }


}