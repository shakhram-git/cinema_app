package com.example.skillcinema.presentation.search_settings_fragments

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.skillcinema.IsCheckedCoverUI
import com.example.skillcinema.R
import com.example.skillcinema.capitalize
import com.example.skillcinema.databinding.ItemCountryGenreListBinding
import com.example.skillcinema.domain.model.Country
import com.example.skillcinema.domain.model.Genre

class GenreCountryListAdapter(
    private val isCountryList: Boolean,
    private val onCountryClick: ((IsCheckedCoverUI<Country>) -> Unit),
    private val onGenreClick: ((IsCheckedCoverUI<Genre>) -> Unit),
    private val isListEmpty: (Boolean) -> Unit,
    private val context: Context
) : Adapter<GenreCountryListAdapter.GenreCountryListItemVH>() {

    class GenreCountryListItemVH(val binding: ItemCountryGenreListBinding) :
        ViewHolder(binding.root)

    private var countryList = emptyList<IsCheckedCoverUI<Country>>()
    private var genreList = emptyList<IsCheckedCoverUI<Genre>>()

    fun setCountryList(countryList: List<IsCheckedCoverUI<Country>>) {
        this.countryList = countryList
        isListEmpty.invoke(itemCount == 0)
        notifyDataSetChanged()
    }

    fun setGenreList(genreList: List<IsCheckedCoverUI<Genre>>) {
        this.genreList = genreList
        isListEmpty.invoke(itemCount == 0)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreCountryListItemVH {
        return GenreCountryListItemVH(
            ItemCountryGenreListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: GenreCountryListItemVH, position: Int) {
        if (isCountryList) {
            val country = countryList[position]
            with(holder.binding) {
                name.text = country.item.country
                if (country.isChecked)
                    root.setBackgroundColor(Color.parseColor("#4DB5B5C9"))
                else root.setBackgroundColor(context.getColor(R.color.white))
                root.setOnClickListener {
                    onCountryClick(country)
                }
            }
        } else {
            val genre = genreList[position]
            with(holder.binding) {
                name.text = genre.item.genre.capitalize()
                if (genre.isChecked)
                    root.setBackgroundColor(Color.parseColor("#4DB5B5C9"))
                else root.setBackgroundColor(context.getColor(R.color.white))
                root.setOnClickListener {
                    onGenreClick(genre)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (isCountryList) {
            countryList.size
        } else {
            genreList.size
        }
    }

}
