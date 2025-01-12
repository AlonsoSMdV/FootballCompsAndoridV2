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
import com.example.footballcompsuserv2.data.remote.players.PlayerCreate
import com.example.footballcompsuserv2.data.remote.players.PlayerRawAttributes
import com.example.footballcompsuserv2.databinding.FragmentCreatePlayerBinding
import com.example.footballcompsuserv2.ui.viewModels.CreatePlayerViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreatePlayerFragment :Fragment(R.layout.fragment_create_player){
    private lateinit var binding: FragmentCreatePlayerBinding
    private val viewModel: CreatePlayerViewModel by viewModels()
    private var idTeam: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreatePlayerBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createPlayerToolbar.apply {
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }


        val btnCreate = view.findViewById<Button>(R.id.create_player)
        btnCreate.setOnClickListener{
            val name = binding.editTextPlayerName.text.toString()
            val firstSurname = binding.editTextPlayerFirstSurname.text.toString()
            val secondSurname = binding.editTextPlayerSecondSurname.text.toString()
            val nationality = binding.editTextPlayerNationality.text.toString()
            val dorsal = binding.editTextPlayerDorsal.text.toString()
            val birthdate = binding.editTextPlayerBirthdate.text.toString()
            val position = binding.editTextPlayerPosition.text.toString()
            idTeam = arguments?.getInt("idTeamSelected")!!

            if (name.isBlank() || firstSurname.isBlank() ||  nationality.isBlank() || dorsal.isBlank() || birthdate.isBlank() || position.isBlank() ) {
                Toast.makeText(requireContext(),"Rellene todos los campos", Toast.LENGTH_SHORT).show()
            }else{
                val createPlayer = PlayerCreate(
                    data = PlayerRawAttributes(
                        name = name,
                        firstSurname = firstSurname,
                        secondSurname = secondSurname,
                        nationality = nationality,
                        dorsal = dorsal.toInt(),
                        birthdate = birthdate,
                        position = position,
                        team = idTeam!!
                    )
                )
                viewModel.CreatePlayer(createPlayer)
                findNavController().navigate(R.id.create_to_players)
            }
        }
    }
}