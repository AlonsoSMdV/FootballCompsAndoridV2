package com.example.footballcompsuserv2.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController

import com.example.footballcompsuserv2.ui.adapters.TeamListAdapter
import com.example.footballcompsuserv2.ui.viewModels.TeamListUiState
import com.example.footballcompsuserv2.ui.viewModels.TeamViewModel
import com.example.footballcompsuserv2.R
import com.example.footballcompsuserv2.databinding.FragmentTeamListBinding

import com.google.android.material.floatingactionbutton.FloatingActionButton

import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.launch

@AndroidEntryPoint
class TeamFragment: Fragment(R.layout.fragment_team_list) {
    private lateinit var binding: FragmentTeamListBinding
    private val viewModel: TeamViewModel by viewModels()
    private var idComp: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTeamListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        idComp = arguments?.getString("idComp")

        val compSelected = idComp!!
        binding = FragmentTeamListBinding.bind(view)

        //Toolbar
        binding.teamsToolbar.apply {
            setNavigationOnClickListener {
                findNavController().navigate(R.id.teams_to_comps)
            }
        }

        //Botónn de navegacion al Fragmento de crear equipos
        val btnToCreate = view.findViewById<FloatingActionButton>(R.id.button_to_create_team)
        btnToCreate.setOnClickListener {
            val action = TeamFragmentDirections.teamsToCreate(compSelected)
            it.findNavController().navigate(action)
        }

        //Adapter para mostrar los equipos
        val adapter = TeamListAdapter(viewModel, compSelected)
        binding.teamList.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observeTeamsByLeague(idComp!!)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        TeamListUiState.Loading -> {
                        }

                        is TeamListUiState.Success -> {
                            adapter.submitList(uiState.teamList)
                        }

                        is TeamListUiState.Error -> {
                        }
                    }
                }
            }
        }
    }
}