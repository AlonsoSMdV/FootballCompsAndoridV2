package com.example.footballcompsuserv2.auth

import android.content.SharedPreferences

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthService @Inject constructor(
    private val sharedPrfc: SharedPreferences
){
    //Funciones mediante sharedPreferences para obtener, guardar y borrar el token
    companion object{
        private const val TKN = "JWT_TOKEN"
    }

    fun getToken(): String?{
        return sharedPrfc.getString(TKN, null)
    }

    fun clearCredentials(){
        sharedPrfc.edit().remove(TKN).remove("USER_ID").apply()
    }

    fun saveToken(token: String){
        sharedPrfc.edit().putString(TKN, token).apply()
    }

    fun saveId(id: String){
        if (id.isNotBlank()){
            sharedPrfc.edit().putString("USER_ID", id).apply()
        }
    }

    fun isAuthenticated(): Boolean{
        return getToken() != null
    }



}