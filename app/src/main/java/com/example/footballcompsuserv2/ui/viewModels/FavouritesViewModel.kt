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
): ViewModel(){
    fun getFavourites() = flow {
        try {
            val favourites = coroutineScope {
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

                val items = mutableListOf<FavouritesAdapter.FavouriteItem>()

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
                items
            }
            emit(favourites)
        } catch (e: Exception) {
            Log.e("FAVORITES", "Error getting favorites", e)
            emit(emptyList())
        }
    }
}