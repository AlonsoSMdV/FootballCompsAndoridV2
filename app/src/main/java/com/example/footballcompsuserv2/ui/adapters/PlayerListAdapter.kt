package com.example.footballcompsuserv2.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import coil3.load

import com.example.footballcompsuserv2.R
import com.example.footballcompsuserv2.ui.fragments.PlayerListFragmentDirections
import com.example.footballcompsuserv2.ui.viewModels.PlayerListViewModel
import com.example.footballcompsuserv2.data.players.Player
import com.example.footballcompsuserv2.databinding.PlayerItemBinding

class PlayerListAdapter(private val viewModel: PlayerListViewModel, private val idTeam: Int): ListAdapter<Player, PlayerListAdapter.PlayerViewHolder>(
    DiffCallback()
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlayerViewHolder {
        val binding = PlayerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayerViewHolder(binding, viewModel, idTeam)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val player = getItem(position)
        holder.bind(player)
    }

    //ViewHolder de jugador
    class PlayerViewHolder(private val binding: PlayerItemBinding, private val viewModel: PlayerListViewModel, private val idTeam: Int): RecyclerView.ViewHolder(binding.root){
        fun bind(player: Player){
            //Nombre
            binding.playerName.text = player.name

            //Img
            if (player.photo != null){
                binding.playerImg.load(player.photo)
            }

            //Botón de borrar
            binding.deletePlayerButton.setOnClickListener {
                viewModel.deletePlayer(player.id.toInt(), idTeam)
            }

            //Botón para hacer al jugador favorito
            binding.buttonPlayersFavs.apply {
                setImageResource(
                    if (player.isFavourite) R.drawable.ic_fav_filled
                    else R.drawable.ic_fav2
                )
                setOnClickListener {
                    viewModel.toggleFavouritePlayers(player, idTeam)
                }
            }

            //Al clickar la card nos lleva a los detalles del jugador
            binding.playerCard.setOnClickListener {
                val action = PlayerListFragmentDirections.playersToDetails(player.id.toInt())
                it.findNavController().navigate(action)
            }
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<Player>(){
        override fun areItemsTheSame(oldItem: Player, newItem: Player): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Player, newItem: Player): Boolean {
            return oldItem == newItem
        }

    }

}