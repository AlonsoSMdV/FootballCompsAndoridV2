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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager

import com.example.footballcompsuserv2.ui.viewModels.CompListUiState
import com.example.footballcompsuserv2.ui.viewModels.CompetitionViewModel
import com.example.footballcompsuserv2.R
import com.example.footballcompsuserv2.databinding.FragmentCompetitionListBinding
import com.example.footballcompsuserv2.ui.adapters.CompetitionListAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.launch

@AndroidEntryPoint
class CompsFragment : Fragment(R.layout.fragment_competition_list) {
    private lateinit var binding: FragmentCompetitionListBinding
    private val viewModel: CompetitionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCompetitionListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //Ponerlo en dos columnas
        binding.compList.layoutManager = GridLayoutManager(requireContext(), 2)

        //Navegar a crear competiciones
        val btnToCreate = view.findViewById<FloatingActionButton>(R.id.button_to_create_comp)
        btnToCreate.setOnClickListener {
            findNavController().navigate(R.id.comps_to_create)
        }

        val adapter = CompetitionListAdapter(viewModel, null)
        binding.compList.adapter = adapter

        // Observamos cambios de ligas
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is CompListUiState.Success -> adapter.submitList(uiState.compList)
                        else -> { /* Ignorar por ahora */ }
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
    }
}