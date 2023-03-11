package com.example.skillcinema.presentation.gallery_fragment

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
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import com.example.skillcinema.PhotoCategory
import com.example.skillcinema.R
import com.example.skillcinema.databinding.FragmentGalleryBinding
import com.example.skillcinema.presentation.ErrorDialogFragment
import com.example.skillcinema.presentation.adapters.DefaultLoadStateAdapter
import com.example.skillcinema.presentation.adapters.PagingGalleryAdapter1
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

@AndroidEntryPoint
class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GalleryViewModel by viewModels()
    private val args: GalleryFragmentArgs by navArgs()
    private lateinit var galleryAdapter: PagingGalleryAdapter1
    private lateinit var footerAdapter: DefaultLoadStateAdapter
    private var filmId by Delegates.notNull<Long>()
    private var photoCategoryKey: String? = null

    private lateinit var animation: Animator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        filmId = args.filmId
        viewModel.getPhotoCategories(filmId)
        galleryAdapter =
            PagingGalleryAdapter1(requireContext(), onPhotoClick = { onPhotoClick(it) })
        footerAdapter = DefaultLoadStateAdapter(requireContext())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        layoutManager.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == galleryAdapter.itemCount && footerAdapter.itemCount > 0 || (position + 1) % 3 == 0)
                    2
                else 1
            }

        }
        galleryAdapter.addLoadStateListener { state ->
            /*binding.recyclerView.isVisible = state.refresh != LoadState.Loading
            binding.progress.isVisible = state.refresh == LoadState.Loading*/
            if (state.refresh is LoadState.Error) {
                ErrorDialogFragment().show(parentFragmentManager, "tag")
            }
            /*if (state.refresh == LoadState.Loading) animation.start()
            else animation.cancel()*/
        }
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = galleryAdapter.withLoadStateFooter(footerAdapter)
        binding.toolbar.setNavigationIcon(R.drawable.icon_caret_left)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        animation = AnimatorInflater.loadAnimator(requireContext(), R.animator.loading_anim)
        animation.setTarget(binding.progress)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.photoCategories.collect { photoCategories ->
                var firstChipId = 0
                photoCategories.forEachIndexed { index, photoCategory ->
                    val chip =
                        layoutInflater.inflate(R.layout.item_chip, binding.chipGroup, false) as Chip
                    if (index == 0) {
                        firstChipId = View.generateViewId()
                        chip.id = firstChipId
                    }
                    chip.tag = photoCategory
                    chip.text = when (photoCategory) {
                        PhotoCategory.STILL -> getString(R.string.stills)
                        PhotoCategory.SHOOTING -> getString(R.string.shooting_photos)
                        PhotoCategory.POSTER -> getString(R.string.posters)
                        PhotoCategory.FAN_ART -> getString(R.string.fan_arts)
                        PhotoCategory.PROMO -> getString(R.string.promo)
                        PhotoCategory.CONCEPT -> getString(R.string.concepts)
                        PhotoCategory.WALLPAPER -> getString(R.string.wallpapers)
                        PhotoCategory.COVER -> getString(R.string.covers)
                        PhotoCategory.SCREENSHOT -> getString(R.string.screenshots)
                    }
                    chip.append("   ")
                    val amountText = SpannableString(photoCategory.amount.toString())
                    amountText.setSpan(
                        AbsoluteSizeSpan(resources.getDimensionPixelOffset(R.dimen.chip_amount_text_size)),
                        0,
                        photoCategory.amount.toString().length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    amountText.setSpan(
                        ForegroundColorSpan(
                            resources.getColor(
                                R.color.light_grey,
                                null
                            )
                        ),
                        0,
                        photoCategory.amount.toString().length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    chip.append(amountText)
                    chip.setOnCheckedChangeListener { button, isChecked ->
                        if (isChecked) {
                            viewModel.getPagingFilmPhotos(
                                filmId,
                                (button.tag as PhotoCategory).keyString
                            )
                            photoCategoryKey = (button.tag as PhotoCategory).keyString
                        }
                        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                            viewModel.pagingPhotos.collect { galleryAdapter.submitData(it) }
                        }
                    }
                    binding.chipGroup.addView(chip)
                }
                binding.chipGroup.check(firstChipId)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.error.collect {
                if (it) ErrorDialogFragment().show(parentFragmentManager, "tag")
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.state.collect { state ->
                binding.progress.isVisible = state == GalleryViewModel.State.LOADING
                binding.content.isVisible = state == GalleryViewModel.State.SUCCESS
                if (state == GalleryViewModel.State.LOADING) animation.start()
                else animation.cancel()
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onPhotoClick(position: Int) {
        val direction = GalleryFragmentDirections.actionToFullScreenGalleryDialogFragment(
            galleryAdapter.snapshot().items.toTypedArray(),
            position
        )
        findNavController().navigate(direction)

    }

}