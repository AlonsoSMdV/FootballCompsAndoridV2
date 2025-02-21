package com.example.footballcompsuserv2.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.footballcompsuserv2.data.players.IPlayerRepository
import com.example.footballcompsuserv2.data.players.Player

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import javax.inject.Inject

@HiltViewModel
class PlayerDetailsViewModel @Inject constructor(
    private val playerRepo: IPlayerRepository
): ViewModel() {
    private val _player = MutableStateFlow<Player?>(null)
    val player = _player.asStateFlow()

    //Leer el jugador seleccionado
    fun getPlayerDetails(playerId: Int) {
        viewModelScope.launch {
            val playerDetails = playerRepo.readOne(playerId)
            _player.value = playerDetails
        }
    }
}