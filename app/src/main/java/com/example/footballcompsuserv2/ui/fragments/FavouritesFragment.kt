package com.example.footballcompsuserv2.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
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

        binding.favCompsList.apply {
            adapter = competitionsAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        binding.favTeamsList.apply {
            adapter = teamsAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        binding.favPlayersList.apply {
            adapter = playersAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        lifecycleScope.launch {
            viewModel.getFavourites().collect { favorites ->
                val competitions = favorites.filterIsInstance<FavouritesAdapter.FavouriteItem.CompetitionItem>()
                val teams = favorites.filterIsInstance<FavouritesAdapter.FavouriteItem.TeamItem>()
                val players = favorites.filterIsInstance<FavouritesAdapter.FavouriteItem.PlayerItem>()

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