package com.example.skillcinema.presentation.film_info_fragment

import android.animation.Animator
import android.animation.AnimatorInflater
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.text.bold
import androidx.core.text.toSpanned
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skillcinema.DefaultCollections
import com.example.skillcinema.R
import com.example.skillcinema.databinding.FragmentFilmInfoBinding
import com.example.skillcinema.domain.model.*
import com.example.skillcinema.presentation.ErrorDialogFragment
import com.example.skillcinema.presentation.adapters.*
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates


@AndroidEntryPoint
class FilmInfoFragment : Fragment() {

    private var _binding: FragmentFilmInfoBinding? = null
    private val binding get() = _binding!!

    private var filmId by Delegates.notNull<Long>()

    private val viewModel: FilmInfoViewModel by viewModels()

    private val args: FilmInfoFragmentArgs by navArgs()

    private lateinit var animation: Animator

    private val actorsListAdapter = StaffListAdapter { onStaffClick(it) }
    private val otherStuffListAdapter = StaffListAdapter { onStaffClick(it) }
    private val galleryAdapter = GalleryAdapter1 { onPhotoClick(it) }
    private val similarFilmsListAdapter = FilmListAdapter1(onFilmClick = { onFilmClick(it) })

    private var isShowToolbar = true
    private var scrollRange = -1
    private var toolbarTitle = ""

    private var isDescriptionCollapsed = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        filmId = args.filmId
        viewModel.getFilmInfo(filmId)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilmInfoBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationIcon(R.drawable.icon_caret_left)
        binding.toolbar.setNavigationOnClickListener {
            if (findNavController().previousBackStackEntry == null)
                Navigation.findNavController(requireActivity(), R.id.tabs_fragment_container)
                    .popBackStack()
            else findNavController().popBackStack()
        }
        animation = AnimatorInflater.loadAnimator(requireContext(), R.animator.loading_anim)
        animation.setTarget(binding.progress)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.seasonsInfo.sectionName.text = getString(R.string.seasons_episodes)

        setActorsList(filmId)

        setOtherStaffList(filmId)

        setGallery(filmId)

        setSimilarFilmsList(filmId)

        setCollectionsButtons()

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.state.collect { state ->
                binding.progress.isVisible = state == FilmInfoViewModel.State.LOADING
                binding.appBar.isVisible = state != FilmInfoViewModel.State.LOADING
                binding.scrollView.isVisible = state == FilmInfoViewModel.State.SUCCESS
                binding.appBarInfo.isVisible = state != FilmInfoViewModel.State.ERROR
                if (state == FilmInfoViewModel.State.LOADING) animation.start()
                else animation.cancel()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.error.collect {
                if (it) ErrorDialogFragment().show(parentFragmentManager, "tag")
            }
        }


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.filmInfo.collect { filmInfo ->
                showAppbarInfo(filmInfo)

                if (filmInfo.seriesInfo?.seasons != null) {
                    binding.seasonsInfo.infoText.text = resources.getString(
                        R.string.seasons_and_episodes_amount,
                        resources.getQuantityString(
                            R.plurals.seasons_count,
                            filmInfo.seriesInfo!!.seasons!! % 10,
                            filmInfo.seriesInfo!!.seasons
                        ),
                        resources.getQuantityString(
                            R.plurals.episodes_count,
                            filmInfo.seriesInfo!!.episodes % 10,
                            filmInfo.seriesInfo!!.episodes
                        )
                    )
                    binding.seasonsInfo.getAllBtn.setOnClickListener {
                        val direction =
                            FilmInfoFragmentDirections.actionFilmInfoFragmentToSeasonsFragment(
                                filmInfo.kinopoiskId, filmInfo.nameRu ?: filmInfo.nameOriginal ?: ""
                            )
                        findNavController().navigate(direction)
                    }
                }
                binding.seasonsInfo.root.isVisible = filmInfo.seriesInfo?.seasons != null
                if (filmInfo.shortDescription != null) binding.shortDescription.text =
                    filmInfo.shortDescription
                binding.shortDescription.isVisible = filmInfo.shortDescription != null
                if (filmInfo.description != null) {
                    binding.description.text = filmInfo.description
                    if (filmInfo.description.length >= 250) {
                        binding.description.text = "${filmInfo.description.take(250)}..."
                        binding.description.setOnClickListener {
                            if (isDescriptionCollapsed)
                                binding.description.text = filmInfo.description
                            else binding.description.text =
                                "${filmInfo.description.take(250)}..."
                            isDescriptionCollapsed = !isDescriptionCollapsed
                        }
                    } else {
                        binding.description.text = filmInfo.description
                    }
                }
                binding.description.isVisible = filmInfo.description != null
            }
        }

        binding.appBar.addOnOffsetChangedListener { barLayout, verticalOffset ->
            if (scrollRange == -1) {
                scrollRange = barLayout?.totalScrollRange!!
            }
            if (scrollRange + verticalOffset == 0) {
                binding.collapsingToolbar.title = toolbarTitle
                binding.toolbar.navigationIcon?.colorFilter =
                    PorterDuffColorFilter(
                        getColor(requireContext(), R.color.black),
                        PorterDuff.Mode.SRC_ATOP
                    )
                isShowToolbar = true
            } else if (isShowToolbar) {
                binding.collapsingToolbar.title = ""
                binding.toolbar.navigationIcon?.colorFilter =
                    PorterDuffColorFilter(
                        getColor(requireContext(), R.color.light_grey),
                        PorterDuff.Mode.SRC_ATOP
                    )
                isShowToolbar = false
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun showAppbarInfo(filmInfo: FilmInfo) {
        toolbarTitle = filmInfo.nameRu ?: filmInfo.nameOriginal ?: ""
        val cover = filmInfo.coverUrl ?: filmInfo.posterUrl
        cover.let {
            Glide.with(requireContext())
                .load(it)
                .into(binding.cover)
        }
        if (filmInfo.logoUrl != null) {
            Glide.with(requireContext())
                .load(filmInfo.logoUrl)
                .into(binding.logo)
        } else {
            val name = filmInfo.nameRu ?: filmInfo.nameOriginal!!
            binding.name.text = name
            binding.name.textSize = if (name.length <= 16) 36f else 20f
        }
        binding.logo.isVisible = filmInfo.logoUrl != null
        binding.name.isVisible = filmInfo.logoUrl == null
        binding.info.text = constructInfoText(filmInfo)
        binding.share.setOnClickListener {
            ShareCompat.IntentBuilder(requireContext())
                .setType("text/plain")
                .setChooserTitle("Share URL")
                .setText(filmInfo.webUrl)
                .startChooser()
        }
        binding.otherCollections.setOnClickListener {
            val direction = FilmInfoFragmentDirections.actionToCollectionsDialogFragment(
                Film(
                    filmInfo.kinopoiskId,
                    filmInfo.nameRu ?: filmInfo.nameOriginal!!,
                    posterUrlPreview = filmInfo.posterUrlPreview,
                    genre = filmInfo.genres.firstOrNull(),
                    premiereRu = null,
                    rating = filmInfo.rating?.toString(),
                    isWatched = false,
                    professionKey = null,
                    year = filmInfo.year
                )
            )
            findNavController().navigate(direction)
        }
    }

    private fun constructInfoText(filmInfo: FilmInfo): Spanned {
        val string = SpannableStringBuilder()
        filmInfo.rating?.let { string.bold { append(filmInfo.rating.toString()) } }
        val name = filmInfo.nameOriginal ?: filmInfo.nameRu
        string.append(" $name\n")
        filmInfo.year?.let { string.append("$it, ") }

        if (filmInfo.genres.isNotEmpty())
            string.append(filmInfo.genres.take(2).joinToString(", "))
        if (filmInfo.seriesInfo?.seasons != null)
            string.append(
                ", ${
                    resources.getQuantityString(
                        R.plurals.seasons_count,
                        filmInfo.seriesInfo!!.seasons!! % 10,
                        filmInfo.seriesInfo!!.seasons
                    )
                }"
            )
        string.append("\n")
        filmInfo.countries.getOrNull(0)?.let { string.append(it) }
        filmInfo.filmLength?.let {
            string.append(", ")
            val hours = (it / 60).toInt()
            val minutes = it % 60
            if (hours != 0) {
                string.append("$hours ч")
            }
            string.append(" $minutes мин")
        }
        filmInfo.ratingAgeLimits?.let { ratingAgeLimits ->
            val limit = ratingAgeLimits.filter { it.digitToIntOrNull() in (0..9) }
            string.append(", $limit+")

        }
        return string.toSpanned()

    }

    private fun onStaffClick(staff: Staff) {
        val direction =
            FilmInfoFragmentDirections.actionFilmInfoFragmentToActorInfoFragment(staff.staffId)
        findNavController().navigate(direction)
    }

    private fun onFilmClick(film: Film) {
        val direction = FilmInfoFragmentDirections.actionToFilmInfoFragment(film.kinopoiskId)
        findNavController().navigate(direction)
    }

    private fun onPhotoClick(position: Int) {
        val direction =
            FilmInfoFragmentDirections.actionToFullScreenGalleryDialogFragment(
                galleryAdapter.getData().toTypedArray(), position
            )
        findNavController().navigate(direction)
    }

    private fun setActorsList(filmId: Long) {
        with(binding.actorsList) {
            collectionName.text = getString(R.string.the_film_starred)

            recyclerView.adapter = actorsListAdapter
            recyclerView.addItemDecoration(BasicGridDivider(requireContext(), 4))
            getAllIcon.visibility = View.VISIBLE
            allOrNumber.setTypeface(null, Typeface.BOLD)
            getAllBtn.setOnClickListener {
                val direction =
                    FilmInfoFragmentDirections.actionToListFragment(StaffListType.Actors(filmId))
                findNavController().navigate(direction)
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.actors.collect {
                    getAllBtn.isVisible = it.size > 20
                    allOrNumber.text = it.size.toString()
                    recyclerView.layoutManager = getGridLayoutManager(it.size, 4)
                    actorsListAdapter.setData(it.take(20))
                    root.isVisible = it.isNotEmpty()

                }
            }
        }
    }

    private fun setOtherStaffList(filmId: Long) {
        with(binding.crewList) {
            collectionName.text = getString(R.string.worked_on_the_film)
            recyclerView.layoutManager =
                GridLayoutManager(requireContext(), 2, RecyclerView.HORIZONTAL, false)
            recyclerView.adapter = otherStuffListAdapter
            recyclerView.addItemDecoration(BasicGridDivider(requireContext(), 2))
            getAllIcon.visibility = View.VISIBLE
            allOrNumber.setTypeface(null, Typeface.BOLD)
            getAllBtn.setOnClickListener {
                val direction = FilmInfoFragmentDirections.actionToListFragment(
                    StaffListType.OtherStaff(filmId)
                )
                findNavController().navigate(direction)
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.otherStaff.collect {
                    getAllBtn.isVisible = it.size > 6
                    allOrNumber.text = it.size.toString()
                    recyclerView.layoutManager = getGridLayoutManager(it.size, 2)
                    otherStuffListAdapter.setData(it.take(6))
                    root.isVisible = it.isNotEmpty()
                }
            }
        }
    }

    private fun setGallery(filmId: Long) {
        with(binding.gallery) {
            collectionName.text = getString(R.string.gallery)
            recyclerView.adapter = galleryAdapter
            recyclerView.addItemDecoration(BasicHorizontalLinearDivider(requireContext()))
            getAllIcon.visibility = View.VISIBLE
            allOrNumber.setTypeface(null, Typeface.BOLD)
            getAllBtn.setOnClickListener {
                val direction =
                    FilmInfoFragmentDirections.actionFilmInfoFragmentToGalleryFragment(filmId)
                findNavController().navigate(direction)
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.galleryPhotos.collect {
                    allOrNumber.text = it.total.toString()
                    getAllBtn.isVisible = it.total > 20
                    galleryAdapter.setData(it.items.take(20))
                    root.isVisible = it.items.isNotEmpty()
                }
            }
        }
    }

    private fun setSimilarFilmsList(filmId: Long) {
        with(binding.similarFilms) {
            collectionName.text = getString(R.string.similar_films)
            recyclerView.adapter = similarFilmsListAdapter
            recyclerView.addItemDecoration(BasicHorizontalLinearDivider(requireContext()))
            getAllIcon.visibility = View.VISIBLE
            allOrNumber.setTypeface(null, Typeface.BOLD)
            getAllBtn.setOnClickListener {
                val direction = FilmInfoFragmentDirections.actionToListFragment(
                    FilmsListType.Similar(toolbarTitle, filmId)
                )
                findNavController().navigate(direction)
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.similarFilms.collect {
                    allOrNumber.text = it.size.toString()
                    getAllBtn.isVisible = it.size > 20
                    similarFilmsListAdapter.submitList(it.take(20))
                    root.isVisible = it.isNotEmpty()
                }
            }
        }
    }

    private fun setCollectionsButtons() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.watchedFilmsIds.collect { ids ->
                if (args.filmId in ids) {
                    binding.addToWatched.setImageResource(R.drawable.icon_eye)
                    binding.addToWatched.setColorFilter(
                        getColor(
                            requireContext(),
                            R.color.light_blue
                        )
                    )
                    binding.addToWatched.setOnClickListener {
                        viewModel.removeFromCollection(
                            DefaultCollections.WATCHED
                        )
                    }
                } else {
                    binding.addToWatched.setImageResource(R.drawable.icon_eye_crossed)
                    binding.addToWatched.setColorFilter(
                        getColor(
                            requireContext(),
                            R.color.light_grey
                        )
                    )
                    binding.addToWatched.setOnClickListener {
                        viewModel.addToCollection(DefaultCollections.WATCHED)
                    }
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.favouriteFilmsIds.collect { ids ->
                if (args.filmId in ids) {
                    binding.addToFavourite.setColorFilter(
                        getColor(
                            requireContext(),
                            R.color.light_blue
                        )
                    )
                    binding.addToFavourite.setOnClickListener {
                        viewModel.removeFromCollection(
                            DefaultCollections.FAVOURITE
                        )
                    }
                } else {
                    binding.addToFavourite.setColorFilter(
                        getColor(
                            requireContext(),
                            R.color.light_grey
                        )
                    )
                    binding.addToFavourite.setOnClickListener {
                        viewModel.addToCollection(DefaultCollections.FAVOURITE)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.desiredFilmsIds.collect { ids ->
                if (args.filmId in ids) {
                    binding.addToDesired.setColorFilter(
                        getColor(
                            requireContext(),
                            R.color.light_blue
                        )
                    )
                    binding.addToDesired.setOnClickListener {
                        viewModel.removeFromCollection(
                            DefaultCollections.DESIRED
                        )
                    }
                } else {
                    binding.addToDesired.setColorFilter(
                        getColor(
                            requireContext(),
                            R.color.light_grey
                        )
                    )
                    binding.addToDesired.setOnClickListener {
                        viewModel.addToCollection(DefaultCollections.DESIRED)
                    }
                }
            }
        }
    }

    private fun getGridLayoutManager(amount: Int, defaultSpanCount: Int): GridLayoutManager {
        val spanCount =
            if (amount == 0) 1 else if (amount < defaultSpanCount) amount else defaultSpanCount
        return GridLayoutManager(requireContext(), spanCount, RecyclerView.HORIZONTAL, false)
    }


}