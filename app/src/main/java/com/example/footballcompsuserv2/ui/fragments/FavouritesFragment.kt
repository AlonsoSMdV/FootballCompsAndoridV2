package com.example.footballcompsuserv2.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.footballcompsuserv2.databinding.FragmentFavouritesBinding
import com.example.footballcompsuserv2.ui.adapters.FavouritesAdapter
import com.example.footballcompsuserv2.ui.viewModels.FavouritesViewModel

import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.launch


@AndroidEntryPoint
class FavouritesFragment : Fragment() {

    private var _binding: FragmentFavouritesBinding? = null
    private val binding get() = _binding!!


    private val competitionsAdapter = FavouritesAdapter()
    private val teamsAdapter = FavouritesAdapter()
    private val playersAdapter = FavouritesAdapter()


    private val viewModel: FavouritesViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mostrar la lista de competiciones favoritas.
        binding.favCompsList.apply {
            adapter = competitionsAdapter // Asignamos el adaptador correspondiente.
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false) // Layout horizontal.
        }

        // Mostrar la lista de equipos favoritos.
        binding.favTeamsList.apply {
            adapter = teamsAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        // Mostrar la lista de jugadores favoritos.
        binding.favPlayersList.apply {
            adapter = playersAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        // Lanzar coroutine en el ciclo de vida del fragmento para observar los datos del ViewModel.
        lifecycleScope.launch {
            viewModel.getFavourites().collect { favorites -> // Recopilar elementos emitidos del ViewModel.

                // Filtrar los elementos según su tipo.
                val competitions = favorites.filterIsInstance<FavouritesAdapter.FavouriteItem.CompetitionItem>()
                val teams = favorites.filterIsInstance<FavouritesAdapter.FavouriteItem.TeamItem>()
                val players = favorites.filterIsInstance<FavouritesAdapter.FavouriteItem.PlayerItem>()

                // Se envían las listas a los adaptadores para actualizar la UI.
                competitionsAdapter.submitList(competitions)
                teamsAdapter.submitList(teams)
                playersAdapter.submitList(players)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
