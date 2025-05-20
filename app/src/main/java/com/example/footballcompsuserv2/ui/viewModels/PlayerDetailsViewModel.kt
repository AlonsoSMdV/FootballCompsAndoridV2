package com.example.footballcompsuserv2.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.footballcompsuserv2.data.leagues.CompetitionFb

import com.example.footballcompsuserv2.data.players.IPlayerRepository
import com.example.footballcompsuserv2.data.players.Player
import com.example.footballcompsuserv2.data.players.PlayerFb
import com.example.footballcompsuserv2.data.teams.ITeamRepository
import com.example.footballcompsuserv2.data.teams.TeamFb
import com.example.footballcompsuserv2.data.user.UserFb

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import javax.inject.Inject

@HiltViewModel
class PlayerDetailsViewModel @Inject constructor(
    private val playerRepo: IPlayerRepository,
    private val teamRepo: ITeamRepository
): ViewModel() {
    private val _player = MutableStateFlow<Player?>(null)
    val player = _player.asStateFlow()

    private val _team = MutableStateFlow<TeamFb?>(null)
    val team: StateFlow<TeamFb?> = _team.asStateFlow()

    //Leer el jugador seleccionado
    fun getPlayerDetails(playerId: Int) {
        viewModelScope.launch {
            val playerDetails = playerRepo.readOne(playerId)
            _player.value = playerDetails
        }
    }

    fun getTeamById(id: String) {
        viewModelScope.launch {
            _team.value = teamRepo.setStreamFb.value.find { it.id == id }
        }
    }

    val playerStream = playerRepo.setStreamFb
}