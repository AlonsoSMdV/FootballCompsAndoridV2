package com.example.footballcompsuserv2.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import coil3.load

import com.example.footballcompsuserv2.R
import com.example.footballcompsuserv2.ui.fragments.TeamFragmentDirections
import com.example.footballcompsuserv2.data.teams.Team
import com.example.footballcompsuserv2.databinding.TeamItemBinding
import com.example.footballcompsuserv2.ui.viewModels.TeamViewModel

class TeamListAdapter(private val viewModel: TeamViewModel, private val idComp: Int): ListAdapter<Team, TeamListAdapter.TeamViewHolder>(
    DiffCallback()
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TeamViewHolder {
        val binding = TeamItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TeamViewHolder(binding, viewModel, idComp)
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        val team = getItem(position)
        holder.bind(team)
    }

    //ViewHolder de equipo
    class TeamViewHolder(private val binding: TeamItemBinding, private val viewModel: TeamViewModel, private val compId: Int): RecyclerView.ViewHolder(binding.root){
        fun bind(team: Team){
            //Nombre
            binding.teamName.text = team.name

            //Img
            if (team.tLogo != null){
                binding.teamImage.load(team.tLogo)
            }

            //Botón de borrar
            binding.deleteTeamButton.setOnClickListener {
                viewModel.deleteTeam(team.id.toInt(), compId)
            }

            //Botón de favoritos
            binding.favouriteButtonTeams.apply {
                setImageResource(
                    if (team.isFavourite) R.drawable.ic_fav_filled
                    else R.drawable.ic_fav2
                )
                setOnClickListener {
                    viewModel.toggleFavouriteTeams(team, compId)
                }
            }

            //Hacer que al clickar en la card navegue a los jugadores del equipo
            binding.teamCard.setOnClickListener {
                val action = TeamFragmentDirections.teamsToPlayers(team.id.toInt())
                it.findNavController().navigate(action)
            }
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<Team>(){
        override fun areItemsTheSame(oldItem: Team, newItem: Team): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Team, newItem: Team): Boolean {
            return oldItem == newItem
        }

    }

}