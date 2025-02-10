package com.example.footballcompsuserv2.ui.fragments

import android.app.ActionBar.LayoutParams
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import com.example.footballcompsuserv2.R
import com.example.footballcompsuserv2.data.user.User
import com.example.footballcompsuserv2.databinding.FragmentProfileDetailsBinding
import com.example.footballcompsuserv2.ui.MainActivity
import com.example.footballcompsuserv2.ui.datastores.ThemePreferences
import com.example.footballcompsuserv2.ui.viewModels.ProfileViewModel
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileDetailsFragment: Fragment(R.layout.fragment_profile_details) {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: FragmentProfileDetailsBinding
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var themePreferences: ThemePreferences
    private lateinit var profThemeToggleButton: Switch

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("football_prefs", 0)
        themePreferences = ThemePreferences(requireContext())

        binding = FragmentProfileDetailsBinding.bind(view)

        viewModel.getActualUser()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.user.collect { user ->
                    user?.let { getUser(it) }
                }
            }
        }
        val btnLogout = view.findViewById<Button>(R.id.btn_logout)
        btnLogout.setOnClickListener {
            logout()
        }

        lifecycleScope.launch {
            themePreferences.themeFlow.collect { isDark ->
                if (isDark) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }

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
    }

    private fun logout(){
        sharedPreferences.edit().clear().apply()

        findNavController().apply {
            navigate(R.id.logout, null, NavOptions.Builder()
                .setPopUpTo(R.id.logout, true)
                .build())
        }
    }
    private fun getUser(user: User){
        binding.userName.setText(user.name)
        binding.userEmail.setText(user.email)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

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
}