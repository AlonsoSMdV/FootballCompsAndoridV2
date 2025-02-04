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
import com.example.footballcompsuserv2.R
import com.example.footballcompsuserv2.databinding.FragmentCompetitionListBinding
import com.example.footballcompsuserv2.databinding.FragmentMatchesBinding
import com.example.footballcompsuserv2.ui.adapters.CompetitionListAdapter
import com.example.footballcompsuserv2.ui.adapters.MatchesAdapter
import com.example.footballcompsuserv2.ui.viewModels.CompListUiState
import com.example.footballcompsuserv2.ui.viewModels.CompetitionViewModel
import com.example.footballcompsuserv2.ui.viewModels.MatchUIState
import com.example.footballcompsuserv2.ui.viewModels.MatchesViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MatchesFragment: Fragment(R.layout.fragment_matches){
    private lateinit var binding: FragmentMatchesBinding
    private val viewModel: MatchesViewModel by viewModels()

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

        val adapter = MatchesAdapter(viewModel)
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