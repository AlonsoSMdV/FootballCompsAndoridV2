package com.example.footballcompsuserv2.ui.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.footballcompsuserv2.data.players.IPlayerRepository
import com.example.footballcompsuserv2.data.players.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PlayerDetailsViewModel @Inject constructor(
    private val playerRepo: IPlayerRepository
): ViewModel(){
    //Clase LiveData sacada de android developers para poder mostrar los detalles de un jugador
    private val _player = MutableLiveData<Player>()
    val player: LiveData<Player> get() = _player

    fun getPlayerDetails(playerId: Int){
        viewModelScope.launch {
            val playerDetails = playerRepo.readOne(playerId)
            _player.value = playerDetails
        }
    }
}