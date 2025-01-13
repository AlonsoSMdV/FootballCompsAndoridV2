package com.example.footballcompsuserv2.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.footballcompsuserv2.R
import com.example.footballcompsuserv2.data.remote.loginRegister.RegisterRaw
import com.example.footballcompsuserv2.ui.MainActivity
import com.example.footballcompsuserv2.ui.viewModels.LoginRegisterViewModel
import com.example.footballcompsuserv2.ui.viewModels.RegisterUIState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment: Fragment(R.layout.fragment_register) {
    private val viewModel: LoginRegisterViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val registerButton = view.findViewById<Button>(R.id.register_btn)

        val toLogin = view.findViewById<TextView>(R.id.to_login)
        (activity as MainActivity).bottomNav.visibility = View.GONE


        registerButton.setOnClickListener {
            val username = view.findViewById<EditText>(R.id.username).text.toString()
            val email = view.findViewById<EditText>(R.id.email).text.toString()
            val password = view.findViewById<EditText>(R.id.password).text.toString()
            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()){
                val registerData = RegisterRaw(username = username, email = email, password = password)
                viewModel.register(registerData)
            }
        }

        lifecycleScope.launch {
            viewModel.registerUIState.collect(){registerUIState ->
                when(registerUIState){
                    RegisterUIState.Loading -> {}
                    is RegisterUIState.Success -> {
                        findNavController().navigate(R.id.register_to_login)
                    }
                    is RegisterUIState.Error -> {}
                }
            }
        }

        toLogin.setOnClickListener{
            findNavController().navigate(R.id.register_to_login)
        }
    }
}