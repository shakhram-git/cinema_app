package com.example.skillcinema.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.skillcinema.R
import com.example.skillcinema.databinding.FragmentDialogErrorBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ErrorDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentDialogErrorBinding? = null
    private val binding get() = _binding!!

    override fun getTheme() = R.style.AppBottomSheetDialogStyle


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDialogErrorBinding.inflate(inflater, container, false)
        binding.exitBtn.setOnClickListener {
            dismiss()
        }
        return binding.root
    }

    override fun onDestroyView(){
        super.onDestroyView()
        _binding = null
    }
}