package com.example.footballcompsuserv2.data.user

import android.util.Log

import com.example.footballcompsuserv2.data.local.ILocalDataSource
import com.example.footballcompsuserv2.data.remote.user.UserRemoteDataSource
import com.example.footballcompsuserv2.di.NetworkUtils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import javax.inject.Inject

//Clase que obtiene los datos del usuario ya sea por remoto o local(si no hay red)
class UserRepository @Inject constructor(
    private val remoteDS: UserRemoteDataSource,
    private val local: ILocalDataSource,
    private val networkUtils: NetworkUtils
) : IUserRepository {

    private val _state = MutableStateFlow<List<User>>(listOf())
    override val setStream: StateFlow<List<User>>
        get() = _state.asStateFlow()

    //Obtener los datos del usuario
    override suspend fun getActualUser(): User {
        var userId = 0;
        return if (networkUtils.isNetworkAvailable()) {//Si hay red los coge del remoto y los guarda en el local
            val res = remoteDS.getActualUser()
            if (res.isSuccessful) {
                val user = res.body()?.toExternal() ?: User("", "", "")
                Log.d("UserRepository", "Usuario obtenido de API: $user")
                local.createLocalUser(user.toLocal()) // Guarda en local
                userId = user.id.toInt()
                user
            } else {
                Log.e("UserRepository", "Error en la API: ${res.errorBody()?.string()}")
                User("", "", "")
            }
        } else {// sino los carga en el local(Hay que arreglar esta parte)
            try {
                val localUser = local.getLocalUser(userId)
                Log.d("UserRepository", "Usuario obtenido de LOCAL: $localUser")
                localUser?.localToExternal() ?: User("", "", "")
            } catch (e: Exception) {
                Log.e("UserRepository", "Error al obtener usuario local", e)
                User("", "", "")
            }
        }
    }
}