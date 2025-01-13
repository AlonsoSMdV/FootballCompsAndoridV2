package com.example.footballcompsuserv2.ui.fragments

import android.content.ContentValues
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.footballcompsuserv2.databinding.FragmentCameraPreviewBinding
import com.example.footballcompsuserv2.ui.MainActivity
import com.example.footballcompsuserv2.ui.viewModels.CreateCompViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executors

@AndroidEntryPoint
class CameraPreviewFragment: Fragment() {
    private lateinit var binding: FragmentCameraPreviewBinding
    private  val viewModel: CreateCompViewModel by activityViewModels()

    private lateinit var cameraController: LifecycleCameraController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCameraPreviewBinding.inflate(
            inflater,
            container,
            false
        )
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
        cameraController.setEnabledUseCases(CameraController.IMAGE_CAPTURE)
        preview.controller = cameraController

        binding.captureButton.setOnClickListener {
            captureImageToDisk()
        }


    }

    /**
     * Función para almacenar la foto tomada en disco
     */
    private fun captureImageToDisk() {
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.getDefault())
            .format(System.currentTimeMillis())

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        }

        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            requireContext().contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        val cameraExecutor = Executors.newSingleThreadScheduledExecutor()
        cameraController.takePicture(
            outputOptions,
            cameraExecutor,
            object: ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {

                    viewLifecycleOwner.lifecycleScope.launch {

                        viewModel.onImageCaptured(outputFileResults.savedUri)

                        findNavController().popBackStack()
                    }

                }

                override fun onError(exception: ImageCaptureException) {
                    exception.message?.let {
                        Toast.makeText(requireContext(),exception.message,Toast.LENGTH_LONG).show()
                    }

                }

            }

        )
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}