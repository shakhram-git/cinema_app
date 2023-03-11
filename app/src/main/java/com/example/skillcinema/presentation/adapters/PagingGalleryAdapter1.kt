package com.example.skillcinema.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skillcinema.databinding.ItemGallery2Binding
import com.example.skillcinema.domain.model.FilmPhoto
import com.example.skillcinema.dpToPixel

class PagingGalleryAdapter1(
    private val context: Context,
    private val onPhotoClick: (Int) -> Unit
) : PagingDataAdapter<FilmPhoto, GalleryItem2ViewHolder>(FilmPhotoDiffCallback()) {
    override fun onBindViewHolder(holder: GalleryItem2ViewHolder, position: Int) {
        val photo = getItem(position)
        with(holder.binding) {
            imgContainer.layoutParams.height =
                if ((position + 1) % 3 == 0) context.dpToPixel(173f).toInt()
                else context.dpToPixel(82f).toInt()
            photo?.let {
                Glide.with(imgContainer.context)
                    .load(photo.previewUrl)
                    .into(imgContainer)
                imgContainer.setOnClickListener {
                    onPhotoClick(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryItem2ViewHolder {
        return GalleryItem2ViewHolder(
            ItemGallery2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }
}

class FilmPhotoDiffCallback : DiffUtil.ItemCallback<FilmPhoto>() {
    override fun areItemsTheSame(oldItem: FilmPhoto, newItem: FilmPhoto): Boolean =
        oldItem == newItem


    override fun areContentsTheSame(oldItem: FilmPhoto, newItem: FilmPhoto): Boolean =
        oldItem == newItem
}

class GalleryItem2ViewHolder(val binding: ItemGallery2Binding) :
    RecyclerView.ViewHolder(binding.root)
