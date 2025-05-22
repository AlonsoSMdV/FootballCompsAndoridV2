package com.example.footballcompsuserv2.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope

import com.example.footballcompsuserv2.R
import com.example.footballcompsuserv2.databinding.FragmentMatchesBinding
import com.example.footballcompsuserv2.di.NetworkUtils
import com.example.footballcompsuserv2.ui.adapters.MatchesAdapter
import com.example.footballcompsuserv2.ui.viewModels.MatchUIState
import com.example.footballcompsuserv2.ui.viewModels.MatchesViewModel

import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MatchesFragment: Fragment(R.layout.fragment_matches){
    private lateinit var binding: FragmentMatchesBinding
    private val viewModel: MatchesViewModel by viewModels()
    @Inject
    lateinit var networkUtils: NetworkUtils

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMatchesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Adapter para mostrar la lista con los datos de los partidos
        val adapter = MatchesAdapter(viewModel, networkUtils)
        binding.matchesList.adapter = adapter

        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                when (uiState) {
                    MatchUIState.Loading -> {
                    }
                    is MatchUIState.Success -> {
                        adapter.submitList(uiState.matchList)
                    }
                    is MatchUIState.Error -> {
                    }
                }
            }
        }
    }
}