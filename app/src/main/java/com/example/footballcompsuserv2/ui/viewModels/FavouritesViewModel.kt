package com.example.footballcompsuserv2.ui.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.footballcompsuserv2.data.leagues.Competition
import com.example.footballcompsuserv2.data.leagues.ICompsRepository
import com.example.footballcompsuserv2.data.players.IPlayerRepository
import com.example.footballcompsuserv2.data.players.Player
import com.example.footballcompsuserv2.data.teams.ITeamRepository
import com.example.footballcompsuserv2.data.teams.Team
import com.example.footballcompsuserv2.ui.adapters.FavoritesAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val compRepo: ICompsRepository,
    private val teamRepo: ITeamRepository,
    private val playerRepo: IPlayerRepository
): ViewModel(){
    fun getFavorites() = flow {
        try {
            val favorites = coroutineScope {
                val competitions = async {
                    compRepo.readFavs(true).also {
                        Log.d("FAVORITES", "Competitions: $it")
                    }
                }
                val teams = async {
                    teamRepo.readFavs(true).also {
                        Log.d("FAVORITES", "Teams: $it")
                    }
                }
                val players = async {
                    playerRepo.readFavs(true).also {
                        Log.d("FAVORITES", "Players: $it")
                    }
                }

                val items = mutableListOf<FavoritesAdapter.FavoriteItem>()

                items.addAll(competitions.await().map {
                    FavoritesAdapter.FavoriteItem.CompetitionItem(it)
                })
                items.addAll(teams.await().map {
                    FavoritesAdapter.FavoriteItem.TeamItem(it)
                })
                items.addAll(players.await().map {
                    FavoritesAdapter.FavoriteItem.PlayerItem(it)
                })

                Log.d("FAVORITES", "Total items: ${items.size}")
                items
            }
            emit(favorites)
        } catch (e: Exception) {
            Log.e("FAVORITES", "Error getting favorites", e)
            emit(emptyList())
        }
    }
}