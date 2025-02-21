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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import coil3.load
import com.example.footballcompsuserv2.R
import com.example.footballcompsuserv2.data.remote.players.PlayerCreate
import com.example.footballcompsuserv2.data.remote.players.PlayerRawAttributes
import com.example.footballcompsuserv2.data.remote.players.TeamData
import com.example.footballcompsuserv2.data.remote.players.TeamId
import com.example.footballcompsuserv2.databinding.FragmentCreatePlayerBinding
import com.example.footballcompsuserv2.ui.viewModels.CreatePlayerViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private var PERMISSIONS_REQUIRED =
    arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO)

@AndroidEntryPoint
class CreatePlayerFragment :Fragment(R.layout.fragment_create_player){
    private var _photoUri: Uri? = null
    private lateinit var binding: FragmentCreatePlayerBinding
    private val viewModel: CreatePlayerViewModel by activityViewModels()
    private var idTeam: Int? = null

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
                loadLogo(uri)
            }
        }
    }

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

    /**
     * Función que navega al fragmento de Preview de camara
     */
    private fun navigateToCamera() {
        val action = CreatePlayerFragmentDirections.createPlayerToCamera()
        findNavController().navigate(action)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.playerToCamera.setOnClickListener{
            if(hasCameraPermissions(requireContext())){
                // navigateToCamera()

            }else {
                launcher.launch(PERMISSIONS_REQUIRED)
            }
        }

        binding.createPlayerToolbar.apply {
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }

        binding.playerSelectPhotoBtn.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest((ActivityResultContracts.PickVisualMedia.ImageOnly)))
        }

        val btnCamera = view.findViewById<Button>(R.id.player_to_camera)
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
                            loadLogo(photoUri)

                        }
                    }
                }
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
                        team = TeamData(TeamId(idTeam!!)),
                        isFavourite = false
                    )
                )
                viewModel.createPlayer(createPlayer, _photoUri)
                findNavController().navigate(R.id.create_to_players)
            }
        }
    }

    private fun loadLogo(uri:Uri?) {
        binding.playerImageView.load(uri)
        _photoUri = uri
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