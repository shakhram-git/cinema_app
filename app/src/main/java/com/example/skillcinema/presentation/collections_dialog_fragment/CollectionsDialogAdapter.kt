package com.example.skillcinema.presentation.collections_dialog_fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.skillcinema.R
import com.example.skillcinema.databinding.ItemFilmCollection1Binding
import com.example.skillcinema.domain.model.LocalCollection

class CollectionsDialogAdapter(private val filmId: Long, private val onCollectionClick: (List<Long>, Int) -> Unit) :
    ListAdapter<LocalCollection, CollectionsDialogAdapter.CollectionViewHolder1>(
        CollectionDiffCallback()
    ) {
    class CollectionViewHolder1(val binding: ItemFilmCollection1Binding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder1 {
        return CollectionViewHolder1(
            ItemFilmCollection1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: CollectionViewHolder1, position: Int) {
        val collection = getItem(position)
        with(holder) {
            binding.collectionName.text = collection.name
            binding.size.text = collection.filmsIds!!.size.toString()
            if (filmId in collection.filmsIds) {
                binding.checkbox.setImageResource(R.drawable.icon_checkbox_filled)
            } else binding.checkbox.setImageResource(R.drawable.icon_checkbox_empty)
            binding.root.setOnClickListener {
                onCollectionClick(collection.filmsIds, collection.id)
            }
        }
    }
}

class CollectionDiffCallback : DiffUtil.ItemCallback<LocalCollection>() {
    override fun areItemsTheSame(oldItem: LocalCollection, newItem: LocalCollection): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: LocalCollection, newItem: LocalCollection): Boolean {
        return oldItem == newItem
    }

}