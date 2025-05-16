package com.example.footballcompsuserv2.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Switch

import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager

import com.example.footballcompsuserv2.R
import com.example.footballcompsuserv2.auth.NavManager
import com.example.footballcompsuserv2.databinding.ActivityMainBinding
import com.example.footballcompsuserv2.ui.notifications.NotificationWorker
import com.example.footballcompsuserv2.ui.datastores.ThemePreferences
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

import java.util.concurrent.TimeUnit

import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var bottomNav: BottomNavigationView
    private lateinit var binding: ActivityMainBinding
    private lateinit var themePreference    : ThemePreferences

    @Inject
    lateinit var navMng: NavManager

    lateinit var themeToggleButton: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)

        //Implementación del datastore y  el boton del activity_main.xml
        themePreference = ThemePreferences(this)

        lifecycleScope.launch {
            themePreference.themeFlow.collect { isDark ->
                if (isDark) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }

        themeToggleButton = findViewById<Switch>(R.id.theme_switch)
        val iconDay = findViewById<ImageView>(R.id.icon_day)
        val iconNight = findViewById<ImageView>(R.id.icon_night)
        //Cambio de tema de la app
        lifecycleScope.launch {
            val currentTheme = themePreference.themeFlow.first()
            themeToggleButton.isChecked = currentTheme
            updateIcons(currentTheme, iconDay, iconNight)
        }

        themeToggleButton.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                themePreference.saveTheme(isChecked)
                updateIcons(isChecked, iconDay, iconNight)
            }
        }

        //NAVEGACIÓN
        val navHostFragment =  supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navCtrl = navHostFragment.navController
        val navInflater = navCtrl.navInflater

        val navGraph = navInflater.inflate(R.navigation.main).apply {
            setStartDestination(
                if (FirebaseAuth.getInstance().currentUser != null){
                    R.id.compsFragment
                }else{
                    R.id.fragmentLogin
                }
            )
        }
        navCtrl.graph = navGraph

        //NavCtrl
        bottomNav = findViewById(R.id.bottom_navigation)
        bottomNav.setupWithNavController(navCtrl)

        navCtrl.addOnDestinationChangedListener{ctrl,dest,_ ->
            if (navMng.navsToLogin(dest.id)){
                ctrl.navigate(R.id.fragmentLogin)
            }

            when(dest.id){
                R.id.fragmentLogin, R.id.fragmentRegister -> {
                    bottomNav.visibility = View.GONE
                    iconDay.visibility = View.VISIBLE
                    iconNight.visibility = View.VISIBLE
                }
                R.id.cameraPreviewFragment -> {
                    bottomNav.visibility = View.GONE
                    themeToggleButton.visibility = View.GONE
                }
                else -> {
                    bottomNav.visibility = View.VISIBLE
                    iconDay.visibility = View.GONE
                    themeToggleButton.visibility = View.VISIBLE
                    iconNight.visibility = View.GONE
                }
            }
        }

        //BottomNav
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_comps -> {
                    navCtrl.navigate(R.id.compsFragment)
                    true
                }
                R.id.navigation_favs -> {
                    navCtrl.navigate(R.id.favouritesFragment)
                    true
                }
                R.id.navigation_matches -> {
                    navCtrl.navigate(R.id.matchesFragment)
                    true
                }
                R.id.navigation_profile -> {
                    navCtrl.navigate(R.id.profileDetails)
                    true
                }
                else -> false
            }
        }

        //NOTIFICACIONES
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val requestPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    startNotificationWorker()
                }
            }

            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                startNotificationWorker() // Ya tiene permisos, iniciar Worker
            }
        } else {
            // En versiones menores a Android 13 no se necesita permiso para notificaciones
            startNotificationWorker()
        }


    }

    //FUNCIÓN CAMBIO COLOR DE ICONOS
    fun updateIcons(isDarkMode: Boolean, iconLeft: ImageView, iconRight: ImageView) {
        val wht = ContextCompat.getColor(this, R.color.white) // Color para el modo activado
        val blck = Color.BLACK // Color negro para el modo desactivado

        if (isDarkMode) {
            iconLeft.setColorFilter(wht) // Modo oscuro: ícono izquierdo blanco
            iconRight.setColorFilter(wht) // Modo oscuro: ícono derecho negro
        } else {
            iconLeft.setColorFilter(blck) // Modo claro: ícono izquierdo negro
            iconRight.setColorFilter(blck) // Modo claro: ícono derecho blanco
        }
    }

    //FUNCIÓN INICIO WORKER DE NOTIFICACIONES
    private fun startNotificationWorker() {
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            15, TimeUnit.MINUTES // Mínimo permitido por WorkManager = 15 mins
        ).setConstraints(
            Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "notification_worker",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}