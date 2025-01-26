package com.example.footballcompsuserv2.auth

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthService @Inject constructor(
    private val sharedPrfc: SharedPreferences
){
    companion object{
        private const val TKN = "JWT_TOKEN"
    }

    fun getToken(): String?{
        return sharedPrfc.getString(TKN, null)
    }

    fun clearToken(){
        sharedPrfc.edit().remove(TKN).apply()
    }

    fun saveToken(token: String){
        sharedPrfc.edit().putString(TKN, token).apply()
    }

    fun isAuthenticated(): Boolean{
        return getToken() != null
    }



}