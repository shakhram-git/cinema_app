package com.example.skillcinema.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skillcinema.databinding.FilmsListFooterWipeBinding
import com.example.skillcinema.databinding.ItemFilmIconTypeOneBinding
import com.example.skillcinema.domain.model.Film

class FilmListAdapterWithFooterWipe(
    private val onFilmClick: (Film) -> Unit,
    private val onFooterBtnClick: () -> Unit,
    private val loadFilmPoster: ((Film) -> Unit)? = null
) : ListAdapter<Film, RecyclerView.ViewHolder>(FilmsDiffCallBack()) {


    private var isFilmsListEmpty: Boolean = true
    fun setIsListEmpty(isEmpty: Boolean) {
        if (this.isFilmsListEmpty != isEmpty) {
            this.isFilmsListEmpty = isEmpty
            notifyDataSetChanged()
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            FOOTER_TYPE -> {
                FilmListFooterWipeViewHolder(
                    FilmsListFooterWipeBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                FilmTypeOneViewHolder(
                    ItemFilmIconTypeOneBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FilmTypeOneViewHolder -> {
                val film = getItem(position)
                with(holder) {
                    binding.downloadBtn.isVisible =
                        film.posterUrlPreview == null && loadFilmPoster != null
                    binding.poster.isVisible = film.posterUrlPreview != null
                    if (film.posterUrlPreview != null) {
                        Glide.with(binding.poster.context)
                            .load(film.posterUrlPreview)
                            .into(binding.poster)
                    } else {
                        if (loadFilmPoster != null) {
                            binding.downloadBtn.setOnClickListener {
                                loadFilmPoster.invoke(film)
                            }
                        }
                    }
                    if (film.rating != null) {
                        binding.rating.visibility = View.VISIBLE
                        binding.rating.text = film.rating
                    } else {
                        binding.rating.visibility = View.GONE
                    }
                    binding.watchedIcon.isVisible = film.isWatched
                    binding.posterGradient.isVisible = film.isWatched
                    binding.name.text = film.name
                    binding.genre.text = film.genre
                    binding.root.setOnClickListener {
                        onFilmClick(film)
                    }
                }
            }
            is FilmListFooterWipeViewHolder -> {
                holder.binding.root.isVisible = !isFilmsListEmpty
                holder.binding.wipeDataBtn.setOnClickListener {
                    onFooterBtnClick.invoke()
                }
            }
        }

    }


    override fun getItemCount(): Int {
        return currentList.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1) {
            FOOTER_TYPE
        } else ITEM_TYPE
    }


    companion object {
        const val ITEM_TYPE = 0
        const val FOOTER_TYPE = 1
    }
}

class FilmListFooterWipeViewHolder(val binding: FilmsListFooterWipeBinding) :
    RecyclerView.ViewHolder(binding.root)