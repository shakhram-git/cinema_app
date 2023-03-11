package com.example.skillcinema.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skillcinema.databinding.ItemFilmIconTypeTwoBinding
import com.example.skillcinema.domain.model.Film

class FilmListAdapter2(
    private val onFilmClick: (Film) -> Unit,
    private val loadFilmPoster: ((Film) -> Unit)? = null
) : ListAdapter<Film, FilmTypeTwoViewHolder>(FilmsDiffCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmTypeTwoViewHolder {
        return FilmTypeTwoViewHolder(
            ItemFilmIconTypeTwoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: FilmTypeTwoViewHolder, position: Int) {
        val film = getItem(position)
        with(holder) {
            binding.downloadBtn.isVisible = film.posterUrlPreview == null && loadFilmPoster != null
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
            var yearAndGenre = ""
            film.year?.let {
                yearAndGenre = it.toString()
                film.genre?.let {
                    yearAndGenre += ", "
                }
            }
            yearAndGenre += film.genre ?: ""
            binding.yearAndGenre.text = yearAndGenre

            /*binding.yearAndGenre.text = film.genre*/

            binding.root.setOnClickListener {
                onFilmClick(film)
            }
        }
    }
}

class FilmTypeTwoViewHolder(val binding: ItemFilmIconTypeTwoBinding) :
    RecyclerView.ViewHolder(binding.root)