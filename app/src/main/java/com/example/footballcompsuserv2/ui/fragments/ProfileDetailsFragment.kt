package com.example.footballcompsuserv2.ui.fragments

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import coil3.load

import dagger.hilt.android.AndroidEntryPoint

import com.example.footballcompsuserv2.R
import com.example.footballcompsuserv2.data.user.User
import com.example.footballcompsuserv2.data.user.UserFb
import com.example.footballcompsuserv2.databinding.FragmentProfileDetailsBinding
import com.example.footballcompsuserv2.ui.datastores.ThemePreferences
import com.example.footballcompsuserv2.ui.viewModels.ProfileViewModel

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// Permisos
private var PERMISSIONS_REQUIRED =
    arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO)


@AndroidEntryPoint
class ProfileDetailsFragment: Fragment(R.layout.fragment_profile_details) {
    private var _photoUri: Uri? = null
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: FragmentProfileDetailsBinding
    private val viewModel: ProfileViewModel by activityViewModels()
    private lateinit var themePreferences: ThemePreferences
    private lateinit var profThemeToggleButton: Switch

    //IMAGES
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

    //Elegir imagen de la galeria
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
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    /**
     * Función que navega al fragmento de Preview de camara
     */
    private fun navigateToCamera() {
        val action = ProfileDetailsFragmentDirections.profileToCamera("profile")
        findNavController().navigate(action)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("football_prefs", 0)
        themePreferences = ThemePreferences(requireContext())

        binding = FragmentProfileDetailsBinding.bind(view)

        binding.userName.isEnabled = false
        binding.userSurname.isEnabled = false
        binding.btnOpenCamera.isEnabled = false
        binding.btnSaveProfile.isEnabled = false

        binding.btnOpenCamera.setOnClickListener{
            if (binding.btnOpenCamera.isEnabled) {
                if (hasCameraPermissions(requireContext())) {
                    navigateToCamera()

                } else {
                    launcher.launch(PERMISSIONS_REQUIRED)
                }
            }else{
                Toast.makeText(
                    requireContext(),
                    "Boton no habilitado",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        //Leer usuario actual
        viewModel.getActualUserFb()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.userFb.collect { user ->
                    user?.let { getUser(it) }
                }

            }
        }
        // Observar foto capturada desde la cámara
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

        setupTextWatchers()

        //Botón de cierre de sesión
        val btnLogout = view.findViewById<Button>(R.id.btn_logout)
        btnLogout.setOnClickListener {
            logout()
        }

        binding.btnEdit.setOnClickListener {
            if (binding.userName.isEnabled && binding.userSurname.isEnabled && binding.btnOpenCamera.isEnabled && binding.btnSaveProfile.isEnabled) {
                binding.userName.isEnabled = false
                binding.userSurname.isEnabled = false
                binding.btnOpenCamera.isEnabled = false
                binding.btnSaveProfile.isEnabled = false
            }else{

                binding.userName.isEnabled = true
                binding.userSurname.isEnabled = true
                binding.btnOpenCamera.isEnabled = true
                binding.btnSaveProfile.isEnabled = true
            }
        }

        //CAMBIO DE TEMA
        lifecycleScope.launch {
            themePreferences.themeFlow.collect { isDark ->
                if (isDark) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }

        //Botón para cambiar el tema a claro u oscuro
        profThemeToggleButton = view.findViewById<Switch>(R.id.prof_theme_switch)
        val iconDay = view.findViewById<ImageView>(R.id.prof_icon_day)
        val iconNight = view.findViewById<ImageView>(R.id.prof_icon_night)
        lifecycleScope.launch {
            val currentTheme = themePreferences.themeFlow.first()
            profThemeToggleButton.isChecked = currentTheme
            updateIcons(currentTheme, iconDay, iconNight)
        }

        profThemeToggleButton.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                themePreferences.saveTheme(isChecked)
                updateIcons(isChecked, iconDay, iconNight)
            }
        }

        binding.btnSaveProfile.setOnClickListener {
            val name = binding.userName.text.toString()
            val surname = binding.userSurname.text.toString()
            val currentUser = viewModel.userFb.value
            if (binding.btnSaveProfile.isEnabled) {
                if (currentUser != null) {
                    val updatedUser = currentUser.copy(
                        name = name,
                        surname = surname
                    )

                    viewLifecycleOwner.lifecycleScope.launch {
                        val result = viewModel.updateUserWithImage(updatedUser, _photoUri)
                        if (result) {
                            Toast.makeText(
                                requireContext(),
                                "Datos actualizados",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Error al actualizar",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }else{
                Toast.makeText(
                    requireContext(),
                    "No hay cambios por hacer",
                    Toast.LENGTH_SHORT
                ).show()
            }

            binding.userName.isEnabled = false
            binding.userSurname.isEnabled = false
            binding.btnOpenCamera.isEnabled = false
            binding.btnSaveProfile.isEnabled = false
        }
    }

    //FUNCIÓN logout/cierre de sesión
    private fun logout(){
        sharedPreferences.edit().clear().apply()

        findNavController().apply {
            navigate(R.id.logout, null, NavOptions.Builder()
                .setPopUpTo(R.id.logout, true)
                .build())
        }
    }

    fun setupTextWatchers() {
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.btnSaveProfile.isEnabled = true
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        binding.userName.addTextChangedListener(watcher)
        binding.userSurname.addTextChangedListener(watcher)
    }


    //FUNCIÓN escribir los datos del usuario actual en el xml
    private fun getUser(user: UserFb){
        binding.userPhoto.load(user.picture)
        binding.userSurname.setText(user.surname)
        binding.userName.setText(user.name)
        binding.userEmail.text = user.email


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.favTeam.collect { team ->
                        team?.let {
                            binding.nameTeam.text = it.name
                            binding.imgTeam.load(it.picture)
                        }
                    }
                }

                launch {
                    viewModel.favPlayer.collect { player ->
                        player?.let {
                            binding.namePlayer.text = it.name
                            binding.imgPlayer.load(it.picture)
                        }
                    }
                }

                launch {
                    viewModel.favLeague.collect { league ->
                        league?.let {
                            binding.nameLeague.text = it.name
                            binding.imgLeague.load(it.picture)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    //Cambio de color en los iconos del tema según el tema
    fun updateIcons(isDarkMode: Boolean, iconLeft: ImageView, iconRight: ImageView) {
        val wht = ContextCompat.getColor(requireContext(), R.color.white) // Color para el modo activado
        val blck = Color.BLACK // Color negro para el modo desactivado

        if (isDarkMode) {
            iconLeft.setColorFilter(wht) // Modo oscuro: ícono izquierdo blanco
            iconRight.setColorFilter(wht) // Modo oscuro: ícono derecho negro
        } else {
            iconLeft.setColorFilter(blck) // Modo claro: ícono izquierdo negro
            iconRight.setColorFilter(blck) // Modo claro: ícono derecho blanco
        }
    }

    //Cargar img
    private fun loadLogo(uri:Uri?) {
        binding.userPhoto.load(uri)
        _photoUri = uri
    }

    //Permisos de camara
    private fun hasCameraPermissions(context: Context):Boolean {
        return PERMISSIONS_REQUIRED.all { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}