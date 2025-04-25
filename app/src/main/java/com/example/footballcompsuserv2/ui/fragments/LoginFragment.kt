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
import com.example.footballcompsuserv2.data.remote.loginRegister.LoginRaw
import com.example.footballcompsuserv2.ui.MainActivity
import com.example.footballcompsuserv2.ui.viewModels.LoginRegisterViewModel
import com.example.footballcompsuserv2.ui.viewModels.LoginUIState

import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
    private val viewModel: LoginRegisterViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loginButton = view.findViewById<Button>(R.id.login_btn)

        //Botón de navegación al registro
        val toRegister = view.findViewById<TextView>(R.id.to_register)
        (activity as MainActivity).bottomNav.visibility = View.GONE

        //Botón para iniciar la sesión
        loginButton.setOnClickListener {
            val username = view.findViewById<EditText>(R.id.username).text.toString()
            val password = view.findViewById<EditText>(R.id.password).text.toString()
            if (username.isNotEmpty() && password.isNotEmpty()){
                val loginData = LoginRaw(identifier = username, password = password)
                viewModel.loginFb(username, password)
            }
        }

        //Si el login es exitoso navega al compsFragment sino muestra error
        lifecycleScope.launch {
            viewModel.loginUIState.collect(){loginUIState ->
                when(loginUIState){
                    LoginUIState.Loading -> {}
                    is LoginUIState.Success -> {
                        findNavController().navigate(R.id.login_to_comps)
                    }
                    is LoginUIState.Error -> {
                        Toast.makeText(context, loginUIState.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        //Botón de navegación al registro
        toRegister.setOnClickListener{
            findNavController().navigate(R.id.login_to_register)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}

