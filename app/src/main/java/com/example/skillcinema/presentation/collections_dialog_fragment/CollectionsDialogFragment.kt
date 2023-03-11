package com.example.skillcinema.presentation.collections_dialog_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import com.bumptech.glide.Glide
import com.example.skillcinema.R
import com.example.skillcinema.databinding.FragmentCollectionsDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CollectionsDialogFragment : BottomSheetDialogFragment() {


    private val viewModel: CollectionsDialogViewModel by viewModels()

    private val args: CollectionsDialogFragmentArgs by navArgs()

    private var _binding: FragmentCollectionsDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CollectionsDialogAdapter

    override fun getTheme() = R.style.AppBottomSheetDialogStyle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = CollectionsDialogAdapter(args.film.kinopoiskId) { filmsIds, collectionId ->
            onCollectionClick(filmsIds, collectionId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollectionsDialogBinding.inflate(inflater, container, false)
        binding.closeBtn.setOnClickListener { dismiss() }
        binding.filmView.downloadBtn.isVisible = false
        Glide.with(requireContext())
            .load(args.film.posterUrlPreview)
            .into(binding.filmView.poster)
        if (args.film.rating != null) {
            binding.filmView.rating.visibility = View.VISIBLE
            binding.filmView.rating.text = args.film.rating
        } else {
            binding.filmView.rating.visibility = View.GONE
        }
        binding.filmView.watchedIcon.isVisible = false
        binding.filmView.posterGradient.isVisible = false
        binding.filmView.name.text = args.film.name
        var yearAndGenre = ""
        args.film.year?.let {
            yearAndGenre = it.toString()
            args.film.genre?.let {
                yearAndGenre += ", "
            }
        }
        yearAndGenre += args.film.genre ?: ""
        binding.filmView.yearAndGenre.text = yearAndGenre

        val itemDecoration = DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(
            ResourcesCompat.getDrawable(resources, R.drawable.list_divider, null)!!
        )
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(itemDecoration)
        binding.createCollectionBtn.setOnClickListener {
            findNavController().navigate(CollectionsDialogFragmentDirections.actionToNewCollectionDialogFragment(args.film))
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.collections.collect { collections ->
                adapter.submitList(collections)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onCollectionClick(filmsIds: List<Long>, collectionId: Int) {
        if (args.film.kinopoiskId in filmsIds)
            viewModel.removeFilmFromCollection(args.film.kinopoiskId, collectionId)
        else viewModel.addFilmToCollection(args.film, collectionId)
    }


}