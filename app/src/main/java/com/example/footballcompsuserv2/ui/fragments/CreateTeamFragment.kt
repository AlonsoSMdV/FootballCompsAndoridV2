package com.example.footballcompsuserv2.ui.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import coil3.load
import com.example.footballcompsuserv2.R
import com.example.footballcompsuserv2.data.remote.teams.TeamCreate
import com.example.footballcompsuserv2.data.remote.teams.TeamRawAttributes
import com.example.footballcompsuserv2.databinding.FragmentCreateTeamBinding
import com.example.footballcompsuserv2.ui.viewModels.CreateTeamViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private var PERMISSIONS_REQUIRED =
    arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO)

@AndroidEntryPoint
class CreateTeamFragment :Fragment(R.layout.fragment_create_team){
    private lateinit var binding: FragmentCreateTeamBinding
    private val viewModel: CreateTeamViewModel by viewModels()
    private var idComp: Int? = null

    private val contract = ActivityResultContracts.RequestMultiplePermissions()

    private val launcher = registerForActivityResult(contract)
    {
            permissions ->
        var granted = true
        permissions.entries.forEach {
                permission ->
            if (permission.key in PERMISSIONS_REQUIRED && !permission.value){
                granted = false
            }
        }
        if (granted){
            //Navigate to camera
            navigateToCamera()

        }else{
            Toast.makeText(requireContext(), "No tiene permisos de camara", Toast.LENGTH_LONG).show()
        }
    }

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){ uri ->
        if (uri != null){
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.onImageCaptured(uri)
            }
        }
    }

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


    /**
     * Función que navega al fragmento de Preview de camara
     */
    private fun navigateToCamera() {
        val action = CreateTeamFragmentDirections.createTeamToCamera()
        findNavController().navigate(action)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.teamToCamera.setOnClickListener{
            if(hasCameraPermissions(requireContext())){
                // navigateToCamera()

            }else {
                launcher.launch(PERMISSIONS_REQUIRED)
            }
        }

        binding.createTeamToolbar.apply {
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }

        binding.teamSelectPhotoBtn.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest((ActivityResultContracts.PickVisualMedia.ImageOnly)))
        }

        val btnCamera = view.findViewById<Button>(R.id.team_to_camera)
        btnCamera.setOnClickListener {
            if (hasCameraPermissions(requireContext())){
                navigateToCamera()
            }else{
                launcher.launch(PERMISSIONS_REQUIRED)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.photo.collect{
                        photoUri ->
                    when(photoUri){
                        Uri.EMPTY -> {
                            //No hay foto, podemos poner un placeholder
                        }
                        else -> {
                            //tenemos la foto, la ponemos en la UI aprovechando coil
                            binding.teamImageView.load(photoUri)
                        }
                    }
                }
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
                        league = idComp!!,
                        isFavourite = false,
                        teamLogo = null
                    )
                )
                viewModel.createTeam(createTeam)
                findNavController().navigate(R.id.create_to_teams)
            }
        }
    }

    private fun hasCameraPermissions(context: Context):Boolean {
        return PERMISSIONS_REQUIRED.all { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}