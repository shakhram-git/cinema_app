package com.example.skillcinema.presentation.home_fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.skillcinema.databinding.CustomListBinding
import com.example.skillcinema.domain.model.Film
import com.example.skillcinema.domain.model.FilmsList
import com.example.skillcinema.domain.model.FilmsListType
import com.example.skillcinema.presentation.adapters.BasicHorizontalLinearDivider
import com.example.skillcinema.presentation.adapters.FilmListAdapterWithFooterMore

class HomeFragmentAdapter(
    private val onMoreBtnClick: (FilmsListType) -> Unit,
    private val onFilmClick: (Film) -> Unit,
    private val context: Context
) :
    ListAdapter<FilmsList, HomeFragmentAdapter.FilmsListViewHolder>(FilmsListDiffCallBack()) {

    class FilmsListViewHolder(val binding: CustomListBinding) : ViewHolder(binding.root)

    class FilmsListDiffCallBack : DiffUtil.ItemCallback<FilmsList>() {
        override fun areItemsTheSame(oldItem: FilmsList, newItem: FilmsList): Boolean {
            return oldItem.type.name == newItem.type.name
        }

        override fun areContentsTheSame(oldItem: FilmsList, newItem: FilmsList): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmsListViewHolder {
        return FilmsListViewHolder(
            CustomListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: FilmsListViewHolder, position: Int) {
        val item = getItem(position)
        val adapter = FilmListAdapterWithFooterMore(onFilmClick, { onMoreBtnClick(item.type) })
        with(holder.binding) {
            collectionName.text = item.type.name
            recyclerView.adapter = adapter
            recyclerView.addItemDecoration(BasicHorizontalLinearDivider(context))
            adapter.submitList(item.films)
            getAllBtn.setOnClickListener { onMoreBtnClick(item.type) }
        }
    }

}