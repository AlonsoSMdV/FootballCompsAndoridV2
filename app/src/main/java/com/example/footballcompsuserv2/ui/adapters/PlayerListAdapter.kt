package com.example.footballcompsuserv2.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

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

class PlayerListAdapter(private val viewModel: PlayerListViewModel, private val idTeam: String, private val idComp: String, private var user: UserFb?,  private var userId: String? ): ListAdapter<PlayerFb, PlayerListAdapter.PlayerViewHolder>(
    DiffCallback()
) {
    fun updateUser(user: UserFb?) {
        this.user = user
        notifyDataSetChanged()  // Redibuja para que se actualicen los botones de favoritos
    }

    fun updateUserId(userId: String?) {
        this.userId = userId
        notifyDataSetChanged()
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
        holder.bind(player, user, userId)
    }

    //ViewHolder de jugador
    class PlayerViewHolder(private val binding: PlayerItemBinding, private val viewModel: PlayerListViewModel, private val idTeam: String,
                           private val idComp: String): RecyclerView.ViewHolder(binding.root){
        fun bind(player: PlayerFb, user: UserFb?, userId: String?){
            //Nombre
            binding.playerName.text = player.name

            //Img
            if (player.picture != null){
                binding.playerImg.load(player.picture)
            }

            // ✅ Usar userId del ViewModel para comprobar propiedad
            val isOwner = player.userId?.id == userId

            binding.deletePlayerButton.alpha = if (isOwner) 1.0f else 0.5f
            binding.updatePlayerButton.alpha = if (isOwner) 1.0f else 0.5f


            //Botón de borrar
            binding.deletePlayerButton.setOnClickListener {
                if (isOwner) {
                    AlertDialog.Builder(binding.root.context)
                        .setTitle("Eliminar jugador")
                        .setMessage("¿Estás seguro de que quieres eliminar el jugador \"${player.name}\"?")
                        .setPositiveButton("Sí") { dialog, _ ->
                            viewModel.deletePlayer(player.id!!, idTeam)
                            dialog.dismiss()
                        }
                        .setNegativeButton("Cancelar") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                } else {
                    Toast.makeText(
                        binding.root.context,
                        "No tienes permisos para eliminar este jugador.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            binding.updatePlayerButton.setOnClickListener {
                if (isOwner) {
                    val action = PlayerListFragmentDirections.playersToUpdate(player.id!!, idTeam, idComp)
                    it.findNavController().navigate(action)
                } else {
                    Toast.makeText(
                        binding.root.context,
                        "No tienes permisos para editar este jugador.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
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