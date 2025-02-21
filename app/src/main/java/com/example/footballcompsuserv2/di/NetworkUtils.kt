package com.example.footballcompsuserv2.di

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

import javax.inject.Inject


// Clase NetworkUtils para verificar la disponibilidad de la red
class NetworkUtils @Inject constructor(
    private val context: Context
) {
    /**
     * Retorna `true` si hay conexión y `false` en caso contrario.
     */
    fun isNetworkAvailable(): Boolean {
        // Servicio de conectividad del sistema
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Si no hay red activa - False
        val network = connectivityManager.activeNetwork ?: return false

        // Capacidades de la red activa, si no hay ninguna, retornar false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        // Verificar los diferentes tipos de conexión disponibles
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true  // Conexión Wi-Fi
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true // Datos móviles
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true // Conexión por cable
            else -> false // No hay conexión disponible
        }
    }
}