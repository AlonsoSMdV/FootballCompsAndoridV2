package com.example.footballcompsuserv2.ui.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel

import com.example.footballcompsuserv2.data.leagues.ICompsRepository
import com.example.footballcompsuserv2.data.players.IPlayerRepository
import com.example.footballcompsuserv2.data.teams.ITeamRepository
import com.example.footballcompsuserv2.ui.adapters.FavouritesAdapter

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow

import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val compRepo: ICompsRepository,
    private val teamRepo: ITeamRepository,
    private val playerRepo: IPlayerRepository
) : ViewModel() {

    // Función para obtener las competiciones, equipos y jugadores favoritos.
    // Uso de Flow para emitir los datos de manera reactiva y asincrónica.
    fun getFavourites() = flow {
        try {
            // Usamos coroutineScope para manejar múltiples llamadas asíncronas.
            val favourites = coroutineScope {
                // Llamadas asíncronas para obtener los datos de favoritos desde cada repositorio.

                val competitions = async {
                    compRepo.readFavs(true).also {
                        Log.d("FAVOURITES", "Competitions: $it")
                    }
                }
                val teams = async {
                    teamRepo.readFavs(true).also {
                        Log.d("FAVOURITES", "Teams: $it")
                    }
                }
                val players = async {
                    playerRepo.readFavs(true).also {
                        Log.d("FAVOURITES", "Players: $it")
                    }
                }

                // Lista mutable para almacenar los elementos favoritos.
                val items = mutableListOf<FavouritesAdapter.FavouriteItem>()

                // Agregamos los elementos de cada tipo a la lista transformándolos en sus respectivas clases de UI.
                items.addAll(competitions.await().map {
                    FavouritesAdapter.FavouriteItem.CompetitionItem(it)
                })
                items.addAll(teams.await().map {
                    FavouritesAdapter.FavouriteItem.TeamItem(it)
                })
                items.addAll(players.await().map {
                    FavouritesAdapter.FavouriteItem.PlayerItem(it)
                })

                Log.d("FAVORITES", "Total items: ${items.size}")
                items // Retornamos la lista de favoritos
            }
            emit(favourites) // Emitimos la lista obtenida para su consumo en la UI.
        } catch (e: Exception) {
            Log.e("FAVORITES", "Error getting favorites", e)
            emit(emptyList()) // Emitimos una lista vacía en caso de error para evitar fallos en la UI.
        }
    }
}
