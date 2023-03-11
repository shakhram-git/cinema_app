package com.example.skillcinema.presentation.adapters

import android.animation.AnimatorInflater
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.skillcinema.R
import com.example.skillcinema.databinding.PagingListStateBinding

class DefaultLoadStateAdapter(private val context: Context) :
    LoadStateAdapter<DefaultLoadStateAdapter.Holder>() {
    class Holder(val binding: PagingListStateBinding) : ViewHolder(binding.root)

    override fun onBindViewHolder(holder: Holder, loadState: LoadState) {
        val animation = AnimatorInflater.loadAnimator(context, R.animator.loading_anim)
        animation.setTarget(holder.binding.progress)
        animation.start()
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): Holder {
        return Holder(
            PagingListStateBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
}