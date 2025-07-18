package com.example.footballcompsuserv2.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager

import com.example.footballcompsuserv2.R
import com.example.footballcompsuserv2.databinding.FragmentPlayerListBinding
import com.example.footballcompsuserv2.ui.adapters.PlayerListAdapter
import com.example.footballcompsuserv2.ui.viewModels.PlayerListUiState
import com.example.footballcompsuserv2.ui.viewModels.PlayerListViewModel

import com.google.android.material.floatingactionbutton.FloatingActionButton

import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlayerListFragment : Fragment(R.layout.fragment_player_list) {
    private lateinit var binding: FragmentPlayerListBinding
    private val viewModel: PlayerListViewModel by viewModels()
    private var idTeam: String? = null
    private var idComp: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayerListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        idTeam = arguments?.getString("idTeam")
        binding = FragmentPlayerListBinding.bind(view)
        val teamId = idTeam!!

        idComp = arguments?.getString("idComp")
        val compId = idComp!!

        //Toolbar
        binding.playersToolbar.apply {
            setNavigationOnClickListener {
                val action = PlayerListFragmentDirections.playersToTeams(compId)
                it.findNavController().navigate(action)
            }
        }

        //Adapter para mostrar los jugadores
        val adapter = PlayerListAdapter(viewModel, teamId, idComp!!, null, null)
        binding.playerList.adapter = adapter

        binding.playerList.layoutManager = GridLayoutManager(requireContext(), 3)

        //Botón de navegación al fragmento de crear jugadores
        val btnToCreate = view.findViewById<FloatingActionButton>(R.id.button_to_create_player)
        btnToCreate.setOnClickListener {
            val action = PlayerListFragmentDirections.playersToCreate(teamId, compId)
            it.findNavController().navigate(action)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observePlayersByTeam(idTeam!!)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when(uiState){
                        PlayerListUiState.Loading ->{

                        }

                        is PlayerListUiState.Success->{
                            adapter.submitList(uiState.playerList)
                        }

                        is PlayerListUiState.Error->{

                        }

                    }
                }
            }
        }

        // Observar usuario (solo actualiza el botón de favoritos)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.user.collect { user ->
                    adapter.updateUser(user)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userId.collect { userId ->
                    adapter.updateUserId(userId)
                }
            }
        }
    }

}