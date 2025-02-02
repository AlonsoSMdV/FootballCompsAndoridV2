package com.example.footballcompsuserv2.ui.fragments

import android.app.ActionBar.LayoutParams
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
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
import com.example.footballcompsuserv2.ui.viewModels.ProfileViewModel
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileDetailsFragment: Fragment(R.layout.fragment_profile_details) {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: FragmentProfileDetailsBinding
    private val viewModel: ProfileViewModel by viewModels()

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
}