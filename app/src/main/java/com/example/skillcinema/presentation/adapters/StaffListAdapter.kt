package com.example.skillcinema.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.example.skillcinema.capitalize
import com.example.skillcinema.databinding.ItemStuffIconBinding
import com.example.skillcinema.domain.model.Staff

class StaffListAdapter(private val onStaffClick: (Staff) -> Unit): Adapter<StaffListAdapter.StuffViewHolder>() {

    class StuffViewHolder(val binding: ItemStuffIconBinding) : RecyclerView.ViewHolder(binding.root)

    private var stuffList = emptyList<Staff>()

    fun setData(stuffList: List<Staff>) {
        this.stuffList = stuffList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StuffViewHolder {
        return StuffViewHolder(
            ItemStuffIconBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: StuffViewHolder, position: Int) {
        val stuff = stuffList[position]
        with(holder){
            Glide.with(binding.photo.context)
                .load(stuff.posterUrl)
                .into(binding.photo)
            binding.name.text = stuff.name
            binding.professionOrRole.text = stuff.description?.capitalize() ?: stuff.professionText.dropLast(1)
            binding.root.setOnClickListener { onStaffClick(stuff) }
        }
    }

    override fun getItemCount(): Int = stuffList.size
}