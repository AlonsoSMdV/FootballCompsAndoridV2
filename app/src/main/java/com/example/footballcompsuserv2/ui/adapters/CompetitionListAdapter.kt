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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

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

            val context = binding.root.context
            val deleteTitle = context.getString(R.string.league_delete)
            val deleteMessage = context.getString(R.string.league_delete_message, competition.name)
            val cancel = context.getString(R.string.cancel)
            val yes = context.getString(R.string.yes)
            val notDelete = context.getString(R.string.league_delete_not)
            val notUpdate = context.getString(R.string.league_update_not)

            // ✅ Usar userId del ViewModel para comprobar propiedad
            val isOwner = competition.userId?.id == userId

            binding.deleteCompButton.alpha = if (isOwner) 1.0f else 0.5f
            binding.updateCompButton.alpha = if (isOwner) 1.0f else 0.5f

            binding.deleteCompButton.setOnClickListener {
                if (isOwner) {
                    MaterialAlertDialogBuilder(binding.root.context)
                        .setTitle(deleteTitle)
                        .setMessage(deleteMessage)
                        .setPositiveButton(yes) { dialog, _ ->
                            viewModel.deleteComp(competition.id!!)
                            dialog.dismiss()
                            Snackbar.make(binding.root, R.string.league_deleted, Snackbar.LENGTH_SHORT)
                                .setAnchorView(R.id.bottom_navigation)
                                .setAction("X"){}
                                .show()
                        }
                        .setNegativeButton(cancel) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()

                } else {
                    Toast.makeText(
                        binding.root.context,
                        notDelete,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            binding.updateCompButton.setOnClickListener {
                if (isOwner) {
                    val action = CompsFragmentDirections.compsToUpdate(competition.id!!)
                    it.findNavController().navigate(action)
                } else {
                    Toast.makeText(
                        binding.root.context,
                        notUpdate,
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