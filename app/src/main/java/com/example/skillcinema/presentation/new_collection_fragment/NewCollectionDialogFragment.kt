package com.example.skillcinema.presentation.new_collection_fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.skillcinema.R
import com.example.skillcinema.databinding.FragmentNewCollectionDialogBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewCollectionDialogFragment : DialogFragment() {

    private var _binding: FragmentNewCollectionDialogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewCollectionDialogViewModel by viewModels()
    private val args: NewCollectionDialogFragmentArgs by navArgs()

    override fun getTheme() = R.style.RoundedCornersDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewCollectionDialogBinding.inflate(inflater, container, false)
        binding.exitBtn.setOnClickListener { dismiss() }
        binding.readyBtn.setOnClickListener {
            if (binding.enterName.text.isNullOrBlank())
                binding.enterName.error = "Введите пожалуйста название"
            else {
                viewModel.createNewCollection(binding.enterName.text.toString(), args?.film)
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.error.collect {
                if (it)
                binding.enterName.error = "Коллекция уже существует"
                else dismiss()
            }
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}