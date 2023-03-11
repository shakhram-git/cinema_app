package com.example.skillcinema.presentation.profile_fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.skillcinema.DefaultCollections
import com.example.skillcinema.R
import com.example.skillcinema.databinding.ItemFilmCollection2Binding
import com.example.skillcinema.domain.model.LocalCollection
import com.example.skillcinema.presentation.collections_dialog_fragment.CollectionDiffCallback

class ProfileCollectionsAdapter(
    private val onCollectionClick: (LocalCollection) -> Unit,
    private val onDeleteBtnClick: (LocalCollection) -> Unit
) :
    ListAdapter<LocalCollection, ProfileCollectionsAdapter.CollectionViewHolder2>(
        CollectionDiffCallback()
    ) {
    class CollectionViewHolder2(val binding: ItemFilmCollection2Binding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder2 {
        return CollectionViewHolder2(
            ItemFilmCollection2Binding.inflate(LayoutInflater.from(parent.context), parent, false)

        )
    }

    override fun onBindViewHolder(holder: CollectionViewHolder2, position: Int) {
        val collection = getItem(position)
        with(holder) {
            binding.root.setOnClickListener {
                if (collection.filmsIds!!.isNotEmpty())
                    onCollectionClick(collection)
            }
            binding.deleteBtn.isVisible = !collection.isDefault
            binding.deleteBtn.setOnClickListener { onDeleteBtnClick(collection) }
            binding.size.text = collection.filmsIds!!.size.toString()
            binding.name.text = collection.name
            when (collection.id) {
                DefaultCollections.FAVOURITE -> binding.icon.setImageResource(R.drawable.icon_heart)
                DefaultCollections.DESIRED -> binding.icon.setImageResource(R.drawable.icon_bookmark)
                else -> binding.icon.setImageResource(R.drawable.icon_user)
            }
        }
    }
}
