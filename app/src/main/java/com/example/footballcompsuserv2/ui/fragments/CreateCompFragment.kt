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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import coil3.load
import com.example.footballcompsuserv2.R
import com.example.footballcompsuserv2.data.remote.leagues.CompCreate
import com.example.footballcompsuserv2.data.remote.leagues.CompRawAttributesMedia
import com.example.footballcompsuserv2.data.remote.leagues.CompRawAtts
import com.example.footballcompsuserv2.databinding.FragmentCreateCompBinding
import com.example.footballcompsuserv2.ui.viewModels.CreateCompViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


private var PERMISSIONS_REQUIRED =
    arrayOf(Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO)

@AndroidEntryPoint
class CreateCompFragment : Fragment(R.layout.fragment_create_comp){
    private var _logoUri: Uri? = null
    private lateinit var binding: FragmentCreateCompBinding
    private val viewModel: CreateCompViewModel by activityViewModels()

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
        binding = FragmentCreateCompBinding.inflate(
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
        val action = CreateCompFragmentDirections.createToCamera()
        findNavController().navigate(action)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.goToCamera.setOnClickListener{
            if(hasCameraPermissions(requireContext())){
                // navigateToCamera()

            }else {
                launcher.launch(PERMISSIONS_REQUIRED)
            }
        }
        binding.createCompsToolbar.apply {
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }

        binding.selectPhotoBtn.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest((ActivityResultContracts.PickVisualMedia.ImageOnly)))
        }

        val btnCamera = view.findViewById<Button>(R.id.go_to_camera)
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
                            binding.compImg.load(photoUri)
                        }
                    }
                }
            }
        }


        val btnCreate = view.findViewById<Button>(R.id.create_comp)
        btnCreate.setOnClickListener{
            val name = binding.editTextCompName.text.toString()

            if (name.isBlank()) {
                Toast.makeText(requireContext(),"Rellene todos los campos", Toast.LENGTH_SHORT).show()
            }else{
                val createComp = CompCreate(
                    data = CompRawAtts(
                        name = name,
                        isFavourite = false
                    )
                )
                viewModel.createComp(createComp, _logoUri)
                findNavController().navigate(R.id.create_to_comps)
            }
        }
    }

    private fun loadLogo(uri:Uri?) {
        binding.compImg.load(uri)
        _logoUri = uri
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