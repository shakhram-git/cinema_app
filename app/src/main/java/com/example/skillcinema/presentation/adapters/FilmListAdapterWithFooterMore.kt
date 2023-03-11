package com.example.skillcinema.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.skillcinema.databinding.FilmsListFooterMoreBinding
import com.example.skillcinema.databinding.ItemFilmIconTypeOneBinding
import com.example.skillcinema.domain.model.Film

class FilmListAdapterWithFooterMore(
    private val onFilmClick: (Film) -> Unit,
    private val onFooterBtnClick: () -> Unit,
    private val loadFilmPoster: ((Film) -> Unit)? = null
) : ListAdapter<Film, ViewHolder>(FilmsDiffCallBack()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            FOOTER_TYPE -> {
                FilmListFooterMoreViewHolder(
                    FilmsListFooterMoreBinding.inflate(
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
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
            is FilmListFooterMoreViewHolder -> {
                holder.binding.moreBtn.setOnClickListener {
                    onFooterBtnClick.invoke()
                }
            }
        }

    }

    override fun getItemCount(): Int =
        if (currentList.size >= 20) currentList.size + 1 else super.getItemCount()

    override fun getItemViewType(position: Int): Int {
        return if (currentList.size >= 20 && position == itemCount - 1) {
            FOOTER_TYPE
        } else ITEM_TYPE
    }


    companion object {
        const val ITEM_TYPE = 0
        const val FOOTER_TYPE = 1
    }
}

class FilmListFooterMoreViewHolder(val binding: FilmsListFooterMoreBinding) :
    ViewHolder(binding.root)

