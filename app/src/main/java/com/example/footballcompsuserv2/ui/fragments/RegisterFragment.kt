package com.example.footballcompsuserv2.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

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

        //Botón de navegación al login
        val toLogin = view.findViewById<TextView>(R.id.to_login)
        (activity as MainActivity).bottomNav.visibility = View.GONE

        //Botón de registar al usuario
        registerButton.setOnClickListener {
            val name = view.findViewById<EditText>(R.id.name).text.toString()
            val surname = view.findViewById<EditText>(R.id.surname).text.toString()
            val email = view.findViewById<EditText>(R.id.email).text.toString()
            val password = view.findViewById<EditText>(R.id.password).text.toString()
            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && surname.isNotEmpty()){
                val registerData = RegisterRaw(username = name, email = email, password = password)
                viewModel.registerFb(email, password, name, surname)
            }
        }

        //Si es exitoso mandar al login
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