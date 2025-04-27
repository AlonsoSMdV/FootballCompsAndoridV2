package com.example.footballcompsuserv2.ui.viewModels

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.footballcompsuserv2.auth.AuthService
import com.example.footballcompsuserv2.data.loginRegister.ILoginRegisterRepo
import com.example.footballcompsuserv2.data.remote.loginRegister.LoginRaw
import com.example.footballcompsuserv2.data.remote.loginRegister.RegisterRaw

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import javax.inject.Inject

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

@HiltViewModel
class LoginRegisterViewModel @Inject constructor(
    private val loginRegisterRepo: ILoginRegisterRepo,
    private val sharedPreferences: SharedPreferences,
    private val authSvc: AuthService
): ViewModel(){
    private val _loginUIState = MutableStateFlow<LoginUIState>(LoginUIState.Loading)
    val loginUIState: StateFlow<LoginUIState>
        get() = _loginUIState.asStateFlow()

    private val _registerUIState = MutableStateFlow<RegisterUIState>(RegisterUIState.Loading)
    val registerUIState: StateFlow<RegisterUIState>
        get() = _registerUIState.asStateFlow()

    //FIREBASE

    fun loginFb(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            val auth = Firebase.auth
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    _loginUIState.value = LoginUIState.Success("Login completado con éxito")
                    Log.d("Success", "Login completo")
                } else {
                    _loginUIState.value = LoginUIState.Error("Ha habido algún error en el login")
                    Log.d("Error", "Login mal completado")
                }
            }
        } else {
            _loginUIState.value = LoginUIState.Error("Credencial/es vacia/s")
            Log.d("Error", "Credenciales vacias")
        }
    }

    fun resetLoginUIState() {
        _loginUIState.value = LoginUIState.Loading
    }

    fun registerFb(email:String, password:String, name: String, surname: String){
        if (email.isNotEmpty() && password.isNotEmpty()){
            val auth = Firebase.auth
            val firestore = FirebaseFirestore.getInstance()

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        val userData = hashMapOf(
                            "name" to name,
                            "surname" to surname,
                            "email" to email,
                            "role" to "User",
                            "picture" to "",
                            "playerFav" to null,
                            "teamFav" to null,
                            "leagueFav" to null,
                            "userId" to user.uid  // 🔥 UID como String, no como referencia
                        )

                        firestore.collection("usuarios").add(userData)
                            .addOnSuccessListener {
                                authSvc.clearCredentials()
                                _registerUIState.value = RegisterUIState.Success()
                            }
                            .addOnFailureListener { e ->
                                _registerUIState.value =
                                    RegisterUIState.Error("Error creando documento Firestore: ${e.localizedMessage}")
                            }
                    } else {
                        _registerUIState.value =
                            RegisterUIState.Error("No se pudo obtener el usuario tras el registro")
                    }
                } else {
                    _registerUIState.value =
                        RegisterUIState.Error("Usuario mal creado: ${task.exception?.localizedMessage}")
                }
            }
        } else {
            _registerUIState.value = RegisterUIState.Error("Faltan credenciales para completar el registro")
        }
    }



    //FUNCIÓN login/inicio de sesión guardando el token del usuario
    fun login(login: LoginRaw){
        viewModelScope.launch {
            try {
                val response = loginRegisterRepo.login(login)

                if (response.isSuccessful){
                    val body = response.body()
                    if (body != null) {
                        authSvc.clearCredentials()

                        authSvc.saveId(body.user.id.toString())
                        authSvc.saveToken(body.jwt)
                        //saveToken(body.jwt)
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

    //FUNCIÓN registro guardando el token del usuario
    fun register(register: RegisterRaw){
        viewModelScope.launch {
            try {
                val response = loginRegisterRepo.register(register)

                if (response.isSuccessful){
                    authSvc.clearCredentials()
                    _registerUIState.value = RegisterUIState.Success()
                }else{
                    val messageError = response.errorBody()?.string()
                    _registerUIState.value = RegisterUIState.Error(
                        "Error registrando: ${response.code()} - $messageError"
                    )
                }
            }catch (e: Exception){
                _registerUIState.value = RegisterUIState.Error("Error desconocido")
            }
        }
    }
}

//UISTATES
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