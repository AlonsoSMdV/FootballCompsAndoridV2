package com.example.footballcompsuserv2.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController

import coil3.load

import com.example.footballcompsuserv2.R
import com.example.footballcompsuserv2.data.players.Player
import com.example.footballcompsuserv2.data.players.PlayerFb
import com.example.footballcompsuserv2.databinding.FragmentPlayersDetailBinding
import com.example.footballcompsuserv2.di.NetworkUtils
import com.example.footballcompsuserv2.ui.viewModels.PlayerDetailsViewModel

import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PlayerDetailsFragment: Fragment(R.layout.fragment_players_detail) {
    private lateinit var binding: FragmentPlayersDetailBinding
    private lateinit var viewModel: PlayerDetailsViewModel
    @Inject
    lateinit var networkUtils: NetworkUtils

    private var playerId: String? = null
    private var teamId: String? = null
    private var compId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayersDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        //Lectura de datos del jugador seleccionado
        viewModel = ViewModelProvider(this).get(PlayerDetailsViewModel::class.java)
        playerId = arguments?.getString("idPlayer")
        teamId = arguments?.getString("idTeam")
        compId = arguments?.getString("idComp")
        //Toolbar
        val toolbar = view.findViewById<Toolbar>(R.id.players_details_toolbar)
        toolbar.setOnClickListener {
            val action = PlayerDetailsFragmentDirections.detailsToPlayers(teamId!!, compId!!)
            it.findNavController().navigate(action)
        }


        playerId?.let { viewModel.loadPlayerById(it) }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playerFb.collect { player ->
                    player?.let { getPlayer(it) }
                }
            }
        }
    }

    //FUNCIÓN escritura de datos obtenidos en el xml
    private fun getPlayer(player: PlayerFb) {
        val pName = context?.getString(R.string.name)
        val pFSurname = context?.getString(R.string.first_surname)
        val pSSurname = context?.getString(R.string.second_surname)
        val pNationality = context?.getString(R.string.nationality)
        val pDorsal = context?.getString(R.string.dorsal)
        val pBirthdate = context?.getString(R.string.birthdate)
        val pPosition = context?.getString(R.string.position)
        if (player.picture!=null){
            binding.playerPhoto.load(player.picture)
        }
        binding.playerName.text = pName+": ${player.name ?: "No disponible"}"
        binding.playerFirstSurname.text = pFSurname+": ${player.firstSurname ?: "No disponible"}"
        binding.playerSecondSurname.text = pSSurname+": ${player.secondSurname ?: "No disponible"}"
        binding.playerNationality.text = pNationality+": ${player.nationality ?: "No disponible"}"
        binding.playerDorsal.text = pDorsal+": ${player.dorsal?.toString() ?: "No disponible"}"
        binding.playerBirthdate.text = pBirthdate+": ${player.birthdate ?: "No disponible"}"
        binding.playerPosition.text = pPosition+": ${player.position ?: "No disponible"}"

        player.nationality?.let {
            if (networkUtils.isNetworkAvailable() && !player.nationality.isNullOrBlank()) {
                showPlayerNationalityOnMap(player.nationality)
            } else {
                binding.mapFragmentContainer.visibility = View.GONE // oculta el contenedor del mapa
                binding.mapOfflineMessage.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Sin conexión. El mapa no se puede mostrar.", Toast.LENGTH_SHORT).show()
            }
        }

        player.team?.let { loadTeamInfo(it.id) }
    }

    private fun showPlayerNationalityOnMap(nationality: String) {
        val mapFragment = MapsFragment()
        val bundle = Bundle().apply {
            putString("place", nationality)
            putFloat("zoomLevel", 4f)
        }
        mapFragment.arguments = bundle

        childFragmentManager.beginTransaction()
            .replace(R.id.map_fragment_container, mapFragment)
            .commit()
    }

    private fun loadTeamInfo(teamId: String) {
        // Primero lanzamos la función del ViewModel para buscar el equipo
        viewModel.getTeamById(teamId)

        // Luego observamos el resultado
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.team.collect { team ->
                    team?.let {
                        binding.playerTeamName.text = it.name ?: "Nombre no disponible"
                        it.picture?.let { pictureUrl ->
                            binding.playerTeamLogo.load(pictureUrl)
                        } ?: binding.playerTeamLogo.setImageResource(R.drawable.ic_no_image)
                    }
                }
            }
        }
    }

}