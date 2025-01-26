package com.example.footballcompsuserv2.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.footballcompsuserv2.data.leagues.Competition
import com.example.footballcompsuserv2.data.players.Player
import com.example.footballcompsuserv2.data.teams.Team
import com.example.footballcompsuserv2.databinding.FavsCompsItemBinding
import com.example.footballcompsuserv2.databinding.FavsPlayersItemBinding
import com.example.footballcompsuserv2.databinding.FavsTeamsItemBinding

class FavouritesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items: List<FavouriteItem> = emptyList()

    // Define different view types
    companion object {
        const val TYPE_COMPETITION = 0
        const val TYPE_TEAM = 1
        const val TYPE_PLAYER = 2
    }

    // Sealed class to handle different types of favorite items
    sealed class FavouriteItem {
        data class CompetitionItem(val competition: Competition) : FavouriteItem()
        data class TeamItem(val team: Team) : FavouriteItem()
        data class PlayerItem(val player: Player) : FavouriteItem()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_COMPETITION -> CompetitionViewHolder(
                FavsCompsItemBinding.inflate(inflater, parent, false)
            )
            TYPE_TEAM -> TeamViewHolder(
                FavsTeamsItemBinding.inflate(inflater, parent, false)
            )
            TYPE_PLAYER -> PlayerViewHolder(
                FavsPlayersItemBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is FavouriteItem.CompetitionItem -> (holder as CompetitionViewHolder).bind(item.competition)
            is FavouriteItem.TeamItem -> (holder as TeamViewHolder).bind(item.team)
            is FavouriteItem.PlayerItem -> (holder as PlayerViewHolder).bind(item.player)
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is FavouriteItem.CompetitionItem -> TYPE_COMPETITION
            is FavouriteItem.TeamItem -> TYPE_TEAM
            is FavouriteItem.PlayerItem -> TYPE_PLAYER
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newItems: List<FavouriteItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    // ViewHolders
    inner class CompetitionViewHolder(
        private val binding: FavsCompsItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(competition: Competition) {
            binding.apply {
                compFavName.text = competition.name
                // Load image using your preferred image loading library
                // Glide.with(itemView).load(competition.logoUrl).into(ivCompetitionLogo)
            }
        }
    }

    inner class TeamViewHolder(
        private val binding: FavsTeamsItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(team: Team) {
            binding.apply {
                teamFavName.text = team.name
                // Load team image
            }
        }
    }

    inner class PlayerViewHolder(
        private val binding: FavsPlayersItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(player: Player) {
            binding.apply {
                playerFavName.text = player.name
                // Load player image and other details
            }
        }
    }
}