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
import com.example.footballcompsuserv2.data.players.PlayerFb
import com.example.footballcompsuserv2.data.user.UserFb
import com.example.footballcompsuserv2.databinding.PlayerItemBinding
import com.example.footballcompsuserv2.ui.fragments.CompsFragmentDirections

class PlayerListAdapter(private val viewModel: PlayerListViewModel, private val idTeam: String, private val idComp: String, private var user: UserFb?): ListAdapter<PlayerFb, PlayerListAdapter.PlayerViewHolder>(
    DiffCallback()
) {
    fun updateUser(user: UserFb?) {
        this.user = user
        notifyDataSetChanged()  // Redibuja para que se actualicen los botones de favoritos
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlayerViewHolder {
        val binding = PlayerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayerViewHolder(binding, viewModel, idTeam, idTeam)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val player = getItem(position)
        holder.bind(player, user)
    }

    //ViewHolder de jugador
    class PlayerViewHolder(private val binding: PlayerItemBinding, private val viewModel: PlayerListViewModel, private val idTeam: String,
                           private val idComp: String): RecyclerView.ViewHolder(binding.root){
        fun bind(player: PlayerFb, user: UserFb?){
            //Nombre
            binding.playerName.text = player.name

            //Img
            if (player.picture != null){
                binding.playerImg.load(player.picture)
            }

            //Botón de borrar
            binding.deletePlayerButton.setOnClickListener {
                viewModel.deletePlayer(player.id.toString(), idTeam)
            }

            binding.updatePlayerButton.setOnClickListener {
                val action = PlayerListFragmentDirections.playersToUpdate(player.id!!, player.team!!.id, idComp)
                it.findNavController().navigate(action)
            }

            // ¿Es favorito?
            val isFavourite = user?.playerFav?.id == player.id
            binding.buttonPlayerFavs.setImageResource(
                if (isFavourite) R.drawable.ic_star_filled else R.drawable.ic_star
            )

            binding.buttonPlayerFavs.setOnClickListener {
                viewModel.setFavouritePlayer(player)
            }

            //Al clickar la card nos lleva a los detalles del jugador
            binding.playerCard.setOnClickListener {
                val action = PlayerListFragmentDirections.playersToDetails(player.id.toString(), player.team!!.id, idComp)
                it.findNavController().navigate(action)
            }
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<PlayerFb>(){
        override fun areItemsTheSame(oldItem: PlayerFb, newItem: PlayerFb): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PlayerFb, newItem: PlayerFb): Boolean {
            return oldItem == newItem
        }

    }

}