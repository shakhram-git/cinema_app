package com.example.skillcinema.presentation.list_fragment

import android.animation.Animator
import android.animation.AnimatorInflater
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.skillcinema.R
import com.example.skillcinema.databinding.FragmentListBinding
import com.example.skillcinema.domain.model.*
import com.example.skillcinema.dpToPixel
import com.example.skillcinema.presentation.ErrorDialogFragment
import com.example.skillcinema.presentation.adapters.DefaultLoadStateAdapter
import com.example.skillcinema.presentation.adapters.FilmListAdapter1
import com.example.skillcinema.presentation.adapters.PagingFilmListAdapter1
import com.example.skillcinema.presentation.adapters.StaffListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListFragment : Fragment() {


    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListViewModel by viewModels()
    private val args: ListFragmentArgs by navArgs()

    private val filmListAdapter =
        FilmListAdapter1(onFilmClick = { onFilmClick(it) }, loadFilmPoster = { loadFilmPoster(it) })
    private val pagingFilmListAdapter = PagingFilmListAdapter1(onFilmClick = { onFilmClick(it) })
    private val staffListAdapter = StaffListAdapter { onStaffClick(it) }
    private lateinit var footerAdapter: DefaultLoadStateAdapter
    private lateinit var animation: Animator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getList(args.listType)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationIcon(R.drawable.icon_caret_left)
        binding.toolbar.setNavigationOnClickListener {
            if (findNavController().previousBackStackEntry == null)
                Navigation.findNavController(requireActivity(), R.id.tabs_fragment_container)
                    .popBackStack()
            else findNavController().popBackStack()
        }
        animation = AnimatorInflater.loadAnimator(requireContext(), R.animator.loading_anim)
        animation.setTarget(binding.progress)
        footerAdapter = DefaultLoadStateAdapter(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.listName.text = args.listType.name

        when (args.listType) {
            is FilmsListType -> {
                binding.recyclerView.addItemDecoration(getGridLayoutDivider())
                when (args.listType.isPaging) {
                    false -> {
                        binding.recyclerView.adapter = filmListAdapter
                        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                            viewModel.filmsList.collect {
                                filmListAdapter.submitList(it)
                            }
                        }
                    }
                    true -> {
                        pagingFilmListAdapter.addLoadStateListener { state ->
                            binding.recyclerView.isVisible = state.refresh != LoadState.Loading
                            binding.progress.isVisible = state.refresh == LoadState.Loading
                            if (state.refresh is LoadState.Error) {
                                ErrorDialogFragment().show(parentFragmentManager, "tag")
                            }
                            if (state.refresh == LoadState.Loading) animation.start()
                            else animation.cancel()
                        }
                        val layoutManager =
                            GridLayoutManager(
                                requireContext(),
                                2,
                                GridLayoutManager.VERTICAL,
                                false
                            )
                        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                            override fun getSpanSize(position: Int): Int {
                                return if (position == pagingFilmListAdapter.itemCount && footerAdapter.itemCount > 0) {
                                    2
                                } else {
                                    1
                                }
                            }
                        }
                        binding.recyclerView.layoutManager = layoutManager
                        binding.recyclerView.adapter = pagingFilmListAdapter.withLoadStateFooter(
                            footerAdapter
                        )
                        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                            viewModel.pagingFilmsList.collect {
                                pagingFilmListAdapter.submitData(it)
                            }
                        }
                    }
                }

            }
            is StaffListType -> {
                binding.recyclerView.adapter = staffListAdapter
                binding.recyclerView.layoutManager =
                    LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                binding.recyclerView.addItemDecoration(getLinearLayoutDivider())

                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    viewModel.staffList.collect {
                        staffListAdapter.setData(it)
                    }
                }

            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                binding.progress.isVisible = it == ListViewModel.State.LOADING
                binding.recyclerView.isVisible = it != ListViewModel.State.LOADING
                if (it == ListViewModel.State.LOADING) animation.start() else animation.cancel()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun onFilmClick(film: Film) {
        val direction = ListFragmentDirections.actionToFilmInfoFragment(film.kinopoiskId)
        findNavController().navigate(direction)
    }

    private fun onStaffClick(staff: Staff) {
        val direction =
            ListFragmentDirections.actionListFragmentToActorInfoFragment(staff.staffId)
        findNavController().navigate(direction)
    }

    private fun loadFilmPoster(film: Film) {
        viewModel.loadFilmPoster(film.kinopoiskId)
    }

    private fun getGridLayoutDivider(): ItemDecoration {
        val space = requireContext().dpToPixel(8f).toInt()
        return object : ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val position = parent.getChildAdapterPosition(view)
                outRect.bottom = space
                if (position % 2 != 0) outRect.right = space * 2
                if (position < 2) outRect.top = space * 4
            }
        }
    }

    private fun getLinearLayoutDivider(): ItemDecoration {
        val space = requireContext().dpToPixel(8f).toInt()
        return object : ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val position = parent.getChildAdapterPosition(view)
                outRect.bottom = space
                if (position == 0) outRect.top = space * 4
            }
        }
    }


}