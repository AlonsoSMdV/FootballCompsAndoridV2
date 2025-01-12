package com.example.footballcompsuserv2.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import com.example.footballcompsuserv2.R

@AndroidEntryPoint
class ProfileDetailsFragment: Fragment(R.layout.fragment_profile_details) {
    private lateinit var sharedPreferences: SharedPreferences
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

        val btnLogout = view.findViewById<Button>(R.id.btn_logout)
        btnLogout.setOnClickListener {
            logout()
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

    override fun onDestroyView() {
        super.onDestroyView()
    }
}