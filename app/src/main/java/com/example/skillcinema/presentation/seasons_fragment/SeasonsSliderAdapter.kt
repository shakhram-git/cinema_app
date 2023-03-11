package com.example.skillcinema.presentation.seasons_fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.skillcinema.R
import com.example.skillcinema.databinding.SlideSeasonBinding
import com.example.skillcinema.domain.model.Seasons
import com.example.skillcinema.presentation.adapters.EpisodeListAdapter

class SeasonsSliderAdapter(val context: Context) :
    Adapter<SeasonsSliderAdapter.SeasonSlideViewHolder>() {
    class SeasonSlideViewHolder(val binding: SlideSeasonBinding) : ViewHolder(binding.root)

    private var seasonsList = emptyList<Seasons.Season>()

    fun setData(seasonsList: List<Seasons.Season>) {
        this.seasonsList = seasonsList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeasonSlideViewHolder {
        val binding = SlideSeasonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SeasonSlideViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SeasonSlideViewHolder, position: Int) {
        val season = seasonsList[position]
        with(holder) {
            binding.seasonsInfo.text = "${
                context.getString(
                    R.string.season_number,
                    season.number
                )
            }, ${
                context.resources.getQuantityString(
                    R.plurals.episodes_count,
                    season.episodes.size,
                    season.episodes.size
                )
            } "
            val adapter = EpisodeListAdapter(context)
            binding.recyclerView.adapter = adapter
            adapter.setData(season.episodes)
        }
    }

    override fun getItemCount() = seasonsList.size
}