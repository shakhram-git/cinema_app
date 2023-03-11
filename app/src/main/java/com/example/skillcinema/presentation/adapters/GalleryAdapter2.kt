package com.example.skillcinema.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.example.skillcinema.databinding.ItemGallery3Binding
import com.example.skillcinema.domain.model.FilmPhoto

class GalleryAdapter2: Adapter<GalleryItem3ViewHolder>() {

    private var imgList = emptyList<FilmPhoto>()

    fun setData(imgList: List<FilmPhoto>) {
        this.imgList = imgList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryItem3ViewHolder {
        return GalleryItem3ViewHolder(
            ItemGallery3Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: GalleryItem3ViewHolder, position: Int) {
        val photo = imgList[position]

        Glide.with(holder.binding.imgContainer.context)
            .load(photo.imageUrl)
            .fitCenter()
            .into(holder.binding.imgContainer)



    }

    override fun getItemCount(): Int = imgList.size
}

class GalleryItem3ViewHolder(val binding: ItemGallery3Binding) :
    RecyclerView.ViewHolder(binding.root)