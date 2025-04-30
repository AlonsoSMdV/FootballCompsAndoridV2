package com.example.footballcompsuserv2.ui.adapters

import android.util.Log
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
import com.example.footballcompsuserv2.data.leagues.CompetitionFb
import com.example.footballcompsuserv2.databinding.CompetitionItemBinding

class CompetitionListAdapter(private val viewModel: CompetitionViewModel): ListAdapter<CompetitionFb, CompetitionListAdapter.CompetitionViewHolder>(
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

        fun bind(competition: CompetitionFb) {
            //Nombre
            binding.compName.text = competition.name

            //id
            binding.compId.text = competition.id

            //Img
            if (competition.picture!=null) {
                binding.compImage.load(competition.picture)
            }

            //Botón de borrar
            binding.deleteCompButton.setOnClickListener{
                viewModel.deleteComp(competition.id!!)
            }

            //Botón de favoritos
            binding.favouriteButton.apply {
                setImageResource(
                    R.drawable.ic_fav2
                )
                setOnClickListener {
                    //viewModel.toggleFavourite(competition)
                }
            }

            //Navegar a los equipos de la liga
            binding.compCard.setOnClickListener {
                val action = CompsFragmentDirections.compsToTeams(competition.id!!)
                it.findNavController().navigate(action)
            }

            binding.deleteCompButton.setOnClickListener {
                viewModel.deleteComp(competition.id!!)
            }

            binding.updateCompButton.setOnClickListener {
                val action = CompsFragmentDirections.compsToUpdate(competition.id!!)
                it.findNavController().navigate(action)
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