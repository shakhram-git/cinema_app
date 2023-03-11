package com.example.skillcinema.presentation.full_screen_gallery_fragment

import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.example.skillcinema.databinding.FragmentFullScreenGalleryBinding
import com.example.skillcinema.presentation.adapters.GalleryAdapter2


class FullScreenGalleryDialogFragment : DialogFragment() {

    private var _binding: FragmentFullScreenGalleryBinding? = null
    private val binding get() = _binding!!

    private val adapter = GalleryAdapter2()
    private val args: FullScreenGalleryDialogFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFullScreenGalleryBinding.inflate(inflater, container, false)
        binding.slider.adapter = adapter
        adapter.setData(args.filmPhotosList.toList())
        binding.slider.setCurrentItem(args.startItemPosition, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //dialog?.setCanceledOnTouchOutside(true)
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}