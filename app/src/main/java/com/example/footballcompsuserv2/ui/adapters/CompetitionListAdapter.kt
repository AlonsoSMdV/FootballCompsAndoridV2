package com.example.footballcompsuserv2.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import coil3.load

import com.example.footballcompsuserv2.R
import com.example.footballcompsuserv2.ui.fragments.CompsFragmentDirections
import com.example.footballcompsuserv2.ui.viewModels.CompetitionViewModel
import com.example.footballcompsuserv2.data.leagues.Competition
import com.example.footballcompsuserv2.databinding.CompetitionItemBinding

class CompetitionListAdapter(private val viewModel: CompetitionViewModel): ListAdapter<Competition, CompetitionListAdapter.CompetitionViewHolder>(
    DiffCallback()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompetitionViewHolder {
        val binding = CompetitionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CompetitionViewHolder(binding, viewModel)
    }

    override fun onBindViewHolder(holder: CompetitionViewHolder, position: Int) {
        val competition = getItem(position)
        holder.bind(competition)
    }

    //ViewHolder de ligas
    class CompetitionViewHolder(private val binding: CompetitionItemBinding, private val viewModel: CompetitionViewModel) : RecyclerView.ViewHolder(binding.root) {

        fun bind(competition: Competition) {
            //Nombre
            binding.compName.text = competition.name

            //id
            binding.compId.text = competition.id

            //Img
            if (competition.logo!=null) {
                binding.compImage.load(competition.logo)
            }

            //Botón de borrar
            binding.deleteCompButton.setOnClickListener{
                viewModel.deleteComp(competition.id.toInt())
            }

            //Botón de favoritos
            binding.favouriteButton.apply {
                setImageResource(
                    if (competition.isFavourite) R.drawable.ic_fav_filled
                    else R.drawable.ic_fav2
                )
                setOnClickListener {
                    viewModel.toggleFavourite(competition)
                }
            }

            //Navegar a los equipos de la liga
            binding.compCard.setOnClickListener {
                val action = CompsFragmentDirections.compsToTeams(competition.id.toInt())
                it.findNavController().navigate(action)
            }

        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Competition>() {
        override fun areItemsTheSame(oldItem: Competition, newItem: Competition): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Competition, newItem: Competition): Boolean {
            return oldItem == newItem
        }
    }
}