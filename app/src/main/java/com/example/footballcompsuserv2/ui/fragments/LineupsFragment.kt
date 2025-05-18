package com.example.footballcompsuserv2.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import coil3.load
import com.example.footballcompsuserv2.R
import com.example.footballcompsuserv2.databinding.FragmentLineupsBinding
import com.example.footballcompsuserv2.databinding.FragmentPlayersDetailBinding
import com.example.footballcompsuserv2.ui.viewModels.LineupsUiState
import com.example.footballcompsuserv2.ui.viewModels.LineupsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LineupsFragment : Fragment(R.layout.fragment_lineups) {
    private lateinit var binding: FragmentLineupsBinding
    private val viewModel: LineupsViewModel by viewModels()
    private val args: LineupsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLineupsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //Toolbar
        val toolbar = view.findViewById<Toolbar>(R.id.lineups_toolbar)
        toolbar.setOnClickListener {
            val action = LineupsFragmentDirections.lineupsToMatches()
            it.findNavController().navigate(action)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is LineupsUiState.Loading -> {
                            // Mostrar loading si quieres
                        }

                        is LineupsUiState.Success -> {
                            val match = state.match

                            binding.localTeamName.text = match.localTeamName
                            binding.visitorTeamName.text = match.visitorTeamName
                            binding.localTeamImage.load(match.localTeamImg)
                            binding.visitorTeamImage.load(match.visitorTeamImg)

                            binding.localPlayersList.removeAllViews()
                            state.localPlayers.forEach { player ->
                                val playerView = layoutInflater.inflate(
                                    R.layout.lineup_player_item,
                                    binding.localPlayersList,
                                    false
                                )
                                playerView.findViewById<TextView>(R.id.player_dorsal).text = player.dorsal?:""
                                playerView.findViewById<TextView>(R.id.player_name).text =
                                    "${player.name ?: ""} ${player.firstSurname ?: ""}"
                                playerView.findViewById<TextView>(R.id.player_position).text =
                                    player.position ?: ""
                                playerView.findViewById<ImageView>(R.id.player_image).load(player.picture)
                                binding.localPlayersList.addView(playerView)
                            }

                            binding.visitorPlayersList.removeAllViews()
                            state.visitorPlayers.forEach { player ->
                                val playerView = layoutInflater.inflate(
                                    R.layout.lineup_player_item,
                                    binding.visitorPlayersList,
                                    false
                                )
                                playerView.findViewById<TextView>(R.id.player_dorsal).text = player.dorsal?:""
                                playerView.findViewById<TextView>(R.id.player_name).text =
                                    "${player.name ?: ""} ${player.firstSurname ?: ""}"
                                playerView.findViewById<TextView>(R.id.player_position).text =
                                    player.position ?: ""
                                playerView.findViewById<ImageView>(R.id.player_image).load(player.picture)
                                binding.visitorPlayersList.addView(playerView)
                            }
                        }

                        is LineupsUiState.Error -> {
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

        viewModel.loadLineups(args.matchId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
