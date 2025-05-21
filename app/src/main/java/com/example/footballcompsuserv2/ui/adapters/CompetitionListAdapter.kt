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
import com.example.footballcompsuserv2.ui.fragments.CompsFragmentDirections
import com.example.footballcompsuserv2.ui.viewModels.CompetitionViewModel
import com.example.footballcompsuserv2.data.leagues.Competition
import com.example.footballcompsuserv2.data.leagues.CompetitionFb
import com.example.footballcompsuserv2.data.user.UserFb
import com.example.footballcompsuserv2.databinding.CompetitionItemBinding

class CompetitionListAdapter(private val viewModel: CompetitionViewModel, private var user: UserFb?, private var userId: String? ): ListAdapter<CompetitionFb, CompetitionListAdapter.CompetitionViewHolder>(
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompetitionViewHolder {
        val binding = CompetitionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CompetitionViewHolder(binding, viewModel)
    }

    override fun onBindViewHolder(holder: CompetitionViewHolder, position: Int) {
        val competition = getItem(position)
        holder.bind(competition, user, userId)
    }


    //ViewHolder de ligas
    class CompetitionViewHolder(private val binding: CompetitionItemBinding, private val viewModel: CompetitionViewModel) : RecyclerView.ViewHolder(binding.root) {

        fun bind(competition: CompetitionFb, user: UserFb?, userId: String?) {
            //Nombre
            binding.compName.text = competition.name



            //Img
            if (competition.picture!=null) {
                binding.compImage.load(competition.picture)
            }


            // ¿Es favorito?
            val isFavourite = user?.leagueFav?.id == competition.id
            binding.favouriteButton.setImageResource(
                if (isFavourite) R.drawable.ic_star_filled else R.drawable.ic_star
            )

            binding.favouriteButton.setOnClickListener {
                viewModel.setFavouriteLeague(competition)
            }

            //Navegar a los equipos de la liga
            binding.compCard.setOnClickListener {
                val action = CompsFragmentDirections.compsToTeams(competition.id!!)
                it.findNavController().navigate(action)
            }

            // ✅ Usar userId del ViewModel para comprobar propiedad
            val isOwner = competition.userId?.id == userId

            binding.deleteCompButton.alpha = if (isOwner) 1.0f else 0.5f
            binding.updateCompButton.alpha = if (isOwner) 1.0f else 0.5f

            binding.deleteCompButton.setOnClickListener {
                if (isOwner) {
                    AlertDialog.Builder(binding.root.context)
                        .setTitle("Eliminar liga")
                        .setMessage("¿Estás seguro de que quieres eliminar la liga \"${competition.name}\"?")
                        .setPositiveButton("Sí") { dialog, _ ->
                            viewModel.deleteComp(competition.id!!)
                            dialog.dismiss()
                        }
                        .setNegativeButton("Cancelar") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()

                    Log.d("DEBUG", "Competition.userId = ${competition.userId?.id}")
                    Log.d("DEBUG", "Current userId = $userId")
                } else {
                    Toast.makeText(
                        binding.root.context,
                        "No tienes permisos para eliminar esta liga.",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("Toast", "SAle el toast")
                }
            }

            binding.updateCompButton.setOnClickListener {
                if (isOwner) {
                    val action = CompsFragmentDirections.compsToUpdate(competition.id!!)
                    it.findNavController().navigate(action)
                } else {
                    Toast.makeText(
                        binding.root.context,
                        "No tienes permisos para editar esta liga.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }


        }
    }

    class DiffCallback : DiffUtil.ItemCallback<CompetitionFb>() {
        override fun areItemsTheSame(oldItem: CompetitionFb, newItem: CompetitionFb): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CompetitionFb, newItem: CompetitionFb): Boolean {
            return oldItem == newItem
        }
    }
}