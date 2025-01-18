package com.example.footballcompsuserv2.ui.viewModels

import androidx.lifecycle.ViewModel
import com.example.footballcompsuserv2.data.leagues.ICompsRepository
import com.example.footballcompsuserv2.data.players.IPlayerRepository
import com.example.footballcompsuserv2.data.teams.ITeamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val compRepo: ICompsRepository,
    private val teamRepo: ITeamRepository,
    private val playerRepo: IPlayerRepository
): ViewModel(){
}