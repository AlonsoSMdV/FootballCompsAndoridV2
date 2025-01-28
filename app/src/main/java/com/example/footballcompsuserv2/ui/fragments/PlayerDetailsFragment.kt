package com.example.footballcompsuserv2.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.footballcompsuserv2.R
import com.example.footballcompsuserv2.data.players.Player
import com.example.footballcompsuserv2.databinding.FragmentPlayersDetailBinding
import com.example.footballcompsuserv2.ui.viewModels.PlayerDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlayerDetailsFragment: Fragment(R.layout.fragment_players_detail) {
    private lateinit var binding: FragmentPlayersDetailBinding
    private lateinit var viewModel: PlayerDetailsViewModel

    private var playerId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayersDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<Toolbar>(R.id.players_details_toolbar)
        toolbar.setOnClickListener {
            findNavController().navigate(R.id.details_to_players)
        }

        viewModel = ViewModelProvider(this).get(PlayerDetailsViewModel::class.java)
        playerId = arguments?.getInt("idPlayer")

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.player.collect { player ->
                    player?.let { getPlayer(it) }
                }
            }
        }

        viewModel.getPlayerDetails(playerId!!)
    }

    private fun getPlayer(player: Player) {
        binding.playerName.text = "Nombre: ${player.name ?: "No disponible"}"
        binding.playerFirstSurname.text = "Primer apellido: ${player.firstSurname ?: "No disponible"}"
        binding.playerSecondSurname.text = "Segundo apellido: ${player.secondSurname ?: "No disponible"}"
        binding.playerNationality.text = "Nacionalidad: ${player.nationality ?: "No disponible"}"
        binding.playerDorsal.text = "Dorsal: ${player.dorsal?.toString() ?: "No disponible"}"
        binding.playerBirthdate.text = "Fecha de nacimiento: ${player.birthdate ?: "No disponible"}"
        binding.playerPosition.text = "Posición: ${player.position ?: "No disponible"}"
    }
}