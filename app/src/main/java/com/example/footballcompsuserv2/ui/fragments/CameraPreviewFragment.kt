package com.example.footballcompsuserv2.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.view.LifecycleCameraController
import androidx.fragment.app.Fragment
import com.example.footballcompsuserv2.databinding.FragmentCameraPreviewBinding
import com.example.footballcompsuserv2.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CameraPreviewFragment: Fragment() {
    private lateinit var binding: FragmentCameraPreviewBinding

    private lateinit var cameraController: LifecycleCameraController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).bottomNav.visibility =View.GONE
        val preview = binding.cameraPreview
        cameraController = LifecycleCameraController(requireContext())
        cameraController.bindToLifecycle(this)
        //Back camera
        cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        preview.controller = cameraController


    }
}