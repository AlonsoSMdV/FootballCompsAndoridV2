package com.example.footballcompsuserv2.ui.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.footballcompsuserv2.R
import com.example.footballcompsuserv2.data.remote.leagues.CompCreate
import com.example.footballcompsuserv2.data.remote.leagues.CompRawAttributes
import com.example.footballcompsuserv2.databinding.FragmentCreateCompBinding
import com.example.footballcompsuserv2.ui.viewModels.CreateCompViewModel
import dagger.hilt.android.AndroidEntryPoint


private var PERMISSIONS_REQUIRED =
    arrayOf(Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO)

@AndroidEntryPoint
class CreateCompFragment : Fragment(R.layout.fragment_create_comp){
    private lateinit var binding: FragmentCreateCompBinding
    private val viewModel: CreateCompViewModel by viewModels()

    val contract = ActivityResultContracts.RequestMultiplePermissions()

    val launcher = registerForActivityResult(contract)
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
        }else{
            Toast.makeText(requireContext(), "No tiene permisos de camara", Toast.LENGTH_LONG).show()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.camara.setOnClickListener{
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


        val btnCreate = view.findViewById<Button>(R.id.create_comp)
        btnCreate.setOnClickListener{
            val name = binding.editTextCompName.text.toString()

            if (name.isBlank()) {
                Toast.makeText(requireContext(),"Rellene todos los campos", Toast.LENGTH_SHORT).show()
            }else{
                val createComp = CompCreate(
                    data = CompRawAttributes(
                        name = name
                    )
                )
                viewModel.CreateComp(createComp)
                findNavController().navigate(R.id.create_to_comps)
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