package com.example.footballcompsuserv2.data.matches

import com.example.footballcompsuserv2.data.local.ILocalDataSource
import com.example.footballcompsuserv2.data.remote.matches.IMatchesRemoteDataSource
import com.example.footballcompsuserv2.di.NetworkUtils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import javax.inject.Inject

//Clase que obtiene del partido ya sea por remoto o local(si no hay red)
class MatchRepository @Inject constructor(
    private val remote: IMatchesRemoteDataSource,
    private val local: ILocalDataSource, // Agregamos la fuente de datos local
    private val networkUtils: NetworkUtils
) : IMatchRepository {

    private val _state = MutableStateFlow<List<Match>>(listOf())
    override val setStream: StateFlow<List<Match>>
        get() = _state.asStateFlow()

    //OBTENER todos los datos
    override suspend fun readAll(): List<Match> {
        val matches = mutableListOf<Match>()
        if (networkUtils.isNetworkAvailable()) {
            //  Si hay internet, obtener datos del servidor
            val res = remote.readAll()
            if (res.isSuccessful) {
                val matchList = res.body()?.data ?: emptyList()
                _state.value = matchList.toExternal()

                //  Guardar los datos en la base de datos local para uso offline
                matchList.forEach { match ->
                    local.createLocalMatch(match.toExternal().toLocal())
                }

                return matchList.toExternal()
            }
        }
        //  Si no hay internet, cargar datos locales
        local.getLocalMatches().collect { localMatches ->
            _state.value = localMatches.localToExternal()
        }

        return matches
    }
}