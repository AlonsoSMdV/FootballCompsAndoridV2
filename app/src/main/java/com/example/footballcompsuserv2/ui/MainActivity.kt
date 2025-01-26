package com.example.footballcompsuserv2.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.footballcompsuserv2.R
import com.example.footballcompsuserv2.auth.NavManager
import com.example.footballcompsuserv2.databinding.ActivityMainBinding
import com.example.footballcompsuserv2.ui.datastores.ThemePreferences
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var bottomNav: BottomNavigationView
    private lateinit var binding: ActivityMainBinding
    private lateinit var themePreference    : ThemePreferences

    @Inject
    lateinit var navMng: NavManager

    lateinit var themeToggleButton: Button

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

        themeToggleButton = findViewById<Button>(R.id.theme_button)
        themeToggleButton.setOnClickListener {
            lifecycleScope.launch {
                val currentTheme = themePreference.themeFlow.first()
                themePreference.saveTheme(!currentTheme)
            }
        }

        val navHostFragment =  supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navCtrl = navHostFragment.navController

        bottomNav = findViewById(R.id.bottom_navigation)
        bottomNav.setupWithNavController(navCtrl)

        navCtrl.addOnDestinationChangedListener{ctrl,dest,_ ->
            if (navMng.navsToLogin(dest.id)){
                ctrl.navigate(R.id.fragmentLogin)
            }

            when(dest.id){
                R.id.fragmentLogin, R.id.fragmentRegister -> {
                    bottomNav.visibility = View.GONE
                }
                R.id.cameraPreviewFragment -> {
                    bottomNav.visibility = View.GONE
                    themeToggleButton.visibility = View.GONE
                }
                else -> {
                    bottomNav.visibility = View.VISIBLE
                    themeToggleButton.visibility = View.VISIBLE
                }
            }
        }

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
                R.id.navigation_profile -> {
                    navCtrl.navigate(R.id.profileDetails)
                    true
                }
                else -> false
            }
        }
    }
}