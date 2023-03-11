package com.example.skillcinema.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.skillcinema.R
import com.example.skillcinema.databinding.ItemEpisodeBinding
import com.example.skillcinema.domain.model.Episode
import java.text.SimpleDateFormat
import java.util.*

class EpisodeListAdapter(val context: Context) : Adapter<EpisodeListAdapter.EpisodeViewHolder>() {

    class EpisodeViewHolder(val binding: ItemEpisodeBinding) : ViewHolder(binding.root)

    private val defaultFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val outputFormat = SimpleDateFormat("d MMMM yyyy", Locale("ru","RU"))

    private var episodeList = emptyList<Episode>()

    fun setData(episodeList: List<Episode>) {
        this.episodeList = episodeList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        return EpisodeViewHolder(
            ItemEpisodeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        val episode = episodeList[position]
        with(holder) {
            binding.info.text = context.getString(
                R.string.episode_number_and_name,
                context.getString(R.string.episode_number, episode.episodeNumber),
                episode.nameRu ?: episode.nameEn ?: ""
            )
            binding.synopsis.isVisible = episode.synopsis != null
            binding.synopsis.text = episode.synopsis
            episode.releaseDate?.let { defaultDate ->
                val date = defaultFormat.parse(defaultDate)
                date?.let {
                    val formattedDate = outputFormat.format(it)
                    binding.date.text = formattedDate
                }
            }
        }
    }

    override fun getItemCount() = episodeList.size
}