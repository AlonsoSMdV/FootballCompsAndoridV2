package com.example.footballcompsuserv2.ui.viewModels

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
class PlayerListViewModel @Inject constructor(
    private val playerRepo: IPlayerRepository
): ViewModel(){
    private val _uiState = MutableStateFlow<PlayerListUiState>(PlayerListUiState.Loading)
    val  uiState: StateFlow<PlayerListUiState>
        get() = _uiState.asStateFlow()

    fun deletePlayer(playerId: Int, teamId: Int){
        viewModelScope.launch {
            playerRepo.deletePlayer(playerId)
            withContext(Dispatchers.IO){
                playerRepo.readPlayersByTeam(teamId)
            }
        }
    }
    init {


        viewModelScope.launch {
            withContext(Dispatchers.IO){
                playerRepo.setStream.collect{
                        playerList ->
                    if (playerList.isEmpty()){
                        _uiState.value = PlayerListUiState.Loading
                    }else{
                        _uiState.value = PlayerListUiState.Success(playerList)
                    }
                }
            }
        }


    }

    fun observePlayersByTeam(teamId:Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                playerRepo.readPlayersByTeam(teamId)
            }
        }
    }

}

sealed class PlayerListUiState(){
    data object Loading: PlayerListUiState()
    class Success(val playerList: List<Player>): PlayerListUiState()
    class Error(val message: String): PlayerListUiState()
}