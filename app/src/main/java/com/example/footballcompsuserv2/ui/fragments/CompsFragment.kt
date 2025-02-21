package com.example.footballcompsuserv2.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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

        //Adapter para cargar los datos de las ligas en la lista
        val adapter = CompetitionListAdapter(viewModel)
        binding.compList.adapter = adapter

        //Ponerlo en dos columnas
        binding.compList.layoutManager = GridLayoutManager(requireContext(), 2)

        //Navegar a crear competiciones
        val btnToCreate = view.findViewById<FloatingActionButton>(R.id.button_to_create_comp)
        btnToCreate.setOnClickListener {
            findNavController().navigate(R.id.comps_to_create)
        }

        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                when (uiState) {
                    CompListUiState.Loading -> {
                    }
                    is CompListUiState.Success -> {
                        adapter.submitList(uiState.compList)
                    }
                    is CompListUiState.Error -> {
                    }
                }
            }
        }
    }
}