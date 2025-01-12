package com.example.footballcompsuserv2.ui.viewModels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.footballcompsuserv2.data.loginRegister.ILoginRegisterRepo
import com.example.footballcompsuserv2.data.remote.loginRegister.LoginRaw
import com.example.footballcompsuserv2.data.remote.loginRegister.RegisterRaw
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginRegisterViewModel @Inject constructor(
    private val loginRegisterRepo: ILoginRegisterRepo,
    private val sharedPreferences: SharedPreferences
): ViewModel(){
    private val _loginUIState = MutableStateFlow<LoginUIState>(LoginUIState.Loading)
    val loginUIState: StateFlow<LoginUIState>
        get() = _loginUIState.asStateFlow()

    private val _registerUIState = MutableStateFlow<RegisterUIState>(RegisterUIState.Loading)
    val registerUIState: StateFlow<RegisterUIState>
        get() = _registerUIState.asStateFlow()

    fun login(login: LoginRaw){
        viewModelScope.launch {
            try {
                val response = loginRegisterRepo.login(login)

                if (response.isSuccessful){
                    val body = response.body()
                    if (body != null) {
                        clearCredentials()

                        saveId(body.user.id.toString())
                        saveToken(body.jwt)
                        _loginUIState.value = LoginUIState.Success(body.user.id.toString())
                    }else{
                        _loginUIState.value = LoginUIState.Error("La respuesta del servidor es errónea")
                    }
                }else{
                    val messageError = "Error del servidor: ${response.code()} - ${response.message()}"
                    _loginUIState.value = LoginUIState.Error(messageError)
                }
            }catch (e: Exception){
                _loginUIState.value = LoginUIState.Error("Error desconocido")
            }
        }
    }

    fun register(register: RegisterRaw){
        viewModelScope.launch {
            try {
                val response = loginRegisterRepo.register(register)

                if (response.isSuccessful){
                    clearCredentials()
                    _registerUIState.value = RegisterUIState.Success()
                }else{
                    val errorMessage = response.errorBody()?.string()
                    _registerUIState.value = RegisterUIState.Error(
                        "Error registrando: ${response.code()} - $errorMessage"
                    )
                }
            }catch (e: Exception){
                _registerUIState.value = RegisterUIState.Error("Error desconocido")
            }
        }
    }

    private fun clearCredentials(){
        sharedPreferences.edit()
            .remove("JWT_TOKEN")
            .remove("USER_ID")
            .apply()
    }

    private fun saveToken(token: String){
        if (token.isNotBlank()){
            sharedPreferences.edit()
                .putString("JWT_TOKEN", token)
                .apply()
        }
    }

     fun getToken(): String?{
         return sharedPreferences.getString("JWT_TOKEN",null)
     }

    fun saveId(id: String){
        if (id.isNotBlank()){
            sharedPreferences.edit()
                .putString("USER_ID", id)
                .apply()
        }
    }

    fun getId(): String? {
        return sharedPreferences.getString("USER_ID", null)
    }
}

sealed class LoginUIState{
    data object Loading: LoginUIState()
    class Success(val message: String): LoginUIState()
    class Error(val message: String): LoginUIState()
}
sealed class RegisterUIState{
    data object Loading: RegisterUIState()
    class Success(): RegisterUIState()
    class Error(val message: String): RegisterUIState()
}