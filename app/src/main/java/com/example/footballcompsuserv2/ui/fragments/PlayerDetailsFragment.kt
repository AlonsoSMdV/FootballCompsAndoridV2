package com.example.footballcompsuserv2.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.footballcompsuserv2.R
import com.example.footballcompsuserv2.data.players.Player
import com.example.footballcompsuserv2.databinding.FragmentPlayersDetailBinding
import com.example.footballcompsuserv2.ui.viewModels.PlayerDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerDetailsFragment: Fragment(R.layout.fragment_players_detail) {
    private lateinit var binding: FragmentPlayersDetailBinding
    private lateinit var  viewModel: PlayerDetailsViewModel

    private var playerId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        viewModel.getPlayerDetails(playerId!!)
        viewModel.player.observe(viewLifecycleOwner, Observer { player ->
            getPlayer(player)
        })



    }

    private fun getPlayer(player: Player){
        binding.playerName.text = player.name ?: "Nombre no disponible"
        binding.playerFirstSurname.text = player.firstSurname ?: "Apellido no disponible"
        binding.playerSecondSurname.text = player.secondSurname ?: "Segundo apellido no disponible"
        binding.playerNationality.text = player.nationality ?: "Nacionalidad no disponible"
        binding.playerDorsal.text = player.dorsal.toString() ?: "Dorsal no disponible"
        binding.playerBirthdate.text = player.birthdate ?: "Fecha de nacimiento no disponible"
        binding.playerPosition.text = player.position ?: "Posición no disponible"
    }
}