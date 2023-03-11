package com.example.skillcinema.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import com.bumptech.glide.Glide
import com.example.skillcinema.databinding.ItemFilmIconTypeOneBinding
import com.example.skillcinema.domain.model.Film

class PagingFilmListAdapter1(
    private val onFilmClick: (Film) -> Unit,
) : PagingDataAdapter<Film, FilmTypeOneViewHolder>(FilmsDiffCallBack()) {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FilmTypeOneViewHolder {
        return FilmTypeOneViewHolder(
            ItemFilmIconTypeOneBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: FilmTypeOneViewHolder, position: Int) {
        val film = getItem(position)
        film?.let {
            with(holder) {
                Glide.with(binding.poster.context)
                    .load(film.posterUrlPreview)
                    .into(binding.poster)

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
    }
}