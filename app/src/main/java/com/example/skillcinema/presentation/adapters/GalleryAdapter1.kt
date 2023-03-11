package com.example.skillcinema.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.example.skillcinema.databinding.ItemGallery1Binding
import com.example.skillcinema.domain.model.FilmPhoto


class GalleryAdapter1(
    private val onPhotoClick: (Int) -> Unit
) : Adapter<GalleryItem1ViewHolder>() {

    private var imgList = emptyList<FilmPhoto>()

    fun setData(imgList: List<FilmPhoto>) {
        this.imgList = imgList
        notifyDataSetChanged()
    }

    fun getData(): List<FilmPhoto> = imgList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryItem1ViewHolder {
        return GalleryItem1ViewHolder(
            ItemGallery1Binding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: GalleryItem1ViewHolder, position: Int) {
        val item = imgList[position]
        with(holder) {
            Glide.with(binding.image)
                .load(item.previewUrl)
                .into(binding.image)
            binding.image.setOnClickListener {
                onPhotoClick(position)
            }
        }
    }

    override fun getItemCount(): Int = imgList.size
}

class GalleryItem1ViewHolder(val binding: ItemGallery1Binding) : RecyclerView.ViewHolder(binding.root)
