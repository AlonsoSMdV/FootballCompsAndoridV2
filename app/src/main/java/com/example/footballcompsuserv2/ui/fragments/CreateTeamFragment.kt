package com.example.footballcompsuserv2.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.footballcompsuserv2.R
import com.example.footballcompsuserv2.data.remote.teams.TeamCreate
import com.example.footballcompsuserv2.data.remote.teams.TeamRawAttributes
import com.example.footballcompsuserv2.databinding.FragmentCreateTeamBinding
import com.example.footballcompsuserv2.ui.viewModels.CreateTeamViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateTeamFragment :Fragment(R.layout.fragment_create_team){
    private lateinit var binding: FragmentCreateTeamBinding
    private val viewModel: CreateTeamViewModel by viewModels()
    private var idComp: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateTeamBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createTeamToolbar.apply {
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }


        val btnCreate = view.findViewById<Button>(R.id.create_team)
        btnCreate.setOnClickListener{
            val name = binding.editTextTeamName.text.toString()
            val nPlayers = binding.editTextTeamNPlayers.text.toString()
            idComp = arguments?.getInt("idCompSelected")!!

            if (name.isBlank() || nPlayers.isBlank()) {
                Toast.makeText(requireContext(),"Rellene todos los campos", Toast.LENGTH_SHORT).show()
            }else{
                val createTeam = TeamCreate(
                    data = TeamRawAttributes(
                        name = name,
                        numberOfPlayers = nPlayers.toInt(),
                        league = idComp!!
                    )
                )
                viewModel.CreateTeam(createTeam)
                findNavController().navigate(R.id.create_to_teams)
            }
        }
    }
}