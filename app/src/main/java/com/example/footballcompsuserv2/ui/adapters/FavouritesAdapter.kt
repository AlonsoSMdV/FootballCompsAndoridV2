package com.example.footballcompsuserv2.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil

import androidx.recyclerview.widget.RecyclerView

import coil3.load

import com.example.footballcompsuserv2.data.leagues.Competition
import com.example.footballcompsuserv2.data.players.Player
import com.example.footballcompsuserv2.data.teams.Team
import com.example.footballcompsuserv2.databinding.FavsCompsItemBinding
import com.example.footballcompsuserv2.databinding.FavsPlayersItemBinding
import com.example.footballcompsuserv2.databinding.FavsTeamsItemBinding

class FavouritesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    // Lista de elementos favoritos, inicialmente vacía
    private var items: List<FavouriteItem> = emptyList()

    // Definimos los diferentes tipos de vista dentro del RecyclerView
    companion object {
        const val TYPE_COMPETITION = 0
        const val TYPE_TEAM = 1
        const val TYPE_PLAYER = 2
    }

    // Sealed class para manejar los diferentes tipos de elementos favoritos
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
            else -> throw IllegalArgumentException("Invalid view type") // Manejo de error en caso de tipo desconocido
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // Determina el tipo de objeto en la posición actual y lo enlaza con su ViewHolder correspondiente
        when (val item = items[position]) {
            is FavouriteItem.CompetitionItem -> (holder as CompetitionViewHolder).bind(item.competition)
            is FavouriteItem.TeamItem -> (holder as TeamViewHolder).bind(item.team)
            is FavouriteItem.PlayerItem -> (holder as PlayerViewHolder).bind(item.player)
        }
    }

    override fun getItemCount(): Int = items.size // Devuelve la cantidad de elementos en la lista

    override fun getItemViewType(position: Int): Int {
        // Determina qué tipo de vista se debe mostrar para un elemento en una posición específica
        return when (items[position]) {
            is FavouriteItem.CompetitionItem -> TYPE_COMPETITION
            is FavouriteItem.TeamItem -> TYPE_TEAM
            is FavouriteItem.PlayerItem -> TYPE_PLAYER
        }
    }

    fun submitList(newItems: List<FavouriteItem>) {
        val diffCallback = FavouriteDiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items = newItems
        diffResult.dispatchUpdatesTo(this) // Aplica solo las diferencias detectadas
    }

    // ViewHolder para competiciones
    inner class CompetitionViewHolder(
        private val binding: FavsCompsItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(competition: Competition) {
            binding.apply {
                compFavName.text = competition.name
                // Cargar la imagen
                if (competition.logo != null) {
                    binding.compFavImage.load(competition.logo)
                }
            }
        }
    }

    // ViewHolder para equipos
    inner class TeamViewHolder(
        private val binding: FavsTeamsItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(team: Team) {
            binding.apply {
                teamFavName.text = team.name
                // Cargar la imagen
                if (team.tLogo != null) {
                    binding.teamFavImage.load(team.tLogo)
                }
            }
        }
    }

    // ViewHolder para jugadores
    inner class PlayerViewHolder(
        private val binding: FavsPlayersItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(player: Player) {
            binding.apply {
                playerFavName.text = player.name
                // Cargar la imagen
                if (player.photo != null) {
                    binding.playerFavImage.load(player.photo)
                }
            }
        }
    }

    // Implementación de DiffUtil para optimizar la actualización de la lista
    class FavouriteDiffCallback(
        private val oldList: List<FavouriteItem>,
        private val newList: List<FavouriteItem>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return when {
                oldList[oldItemPosition] is FavouriteItem.CompetitionItem &&
                        newList[newItemPosition] is FavouriteItem.CompetitionItem -> {
                    (oldList[oldItemPosition] as FavouriteItem.CompetitionItem).competition.id ==
                            (newList[newItemPosition] as FavouriteItem.CompetitionItem).competition.id
                }

                oldList[oldItemPosition] is FavouriteItem.TeamItem &&
                        newList[newItemPosition] is FavouriteItem.TeamItem -> {
                    (oldList[oldItemPosition] as FavouriteItem.TeamItem).team.id ==
                            (newList[newItemPosition] as FavouriteItem.TeamItem).team.id
                }

                oldList[oldItemPosition] is FavouriteItem.PlayerItem &&
                        newList[newItemPosition] is FavouriteItem.PlayerItem -> {
                    (oldList[oldItemPosition] as FavouriteItem.PlayerItem).player.id ==
                            (newList[newItemPosition] as FavouriteItem.PlayerItem).player.id
                }

                else -> false
            }
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}