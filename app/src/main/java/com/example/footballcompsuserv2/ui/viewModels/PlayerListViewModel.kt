package com.example.footballcompsuserv2.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.footballcompsuserv2.data.players.IPlayerRepository
import com.example.footballcompsuserv2.data.players.Player
import com.example.footballcompsuserv2.data.players.PlayerFb
import com.example.footballcompsuserv2.data.remote.players.PlayerRawAttributesMedia
import com.example.footballcompsuserv2.data.remote.players.PlayerUpdate
import com.example.footballcompsuserv2.data.teams.TeamFb
import com.example.footballcompsuserv2.data.user.IUserRepository
import com.example.footballcompsuserv2.data.user.UserFb
import com.google.firebase.firestore.FirebaseFirestore

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
    private val playerRepo: IPlayerRepository,
    private val userRepo: IUserRepository
): ViewModel(){
    private val _uiState = MutableStateFlow<PlayerListUiState>(PlayerListUiState.Loading)
    val  uiState: StateFlow<PlayerListUiState>
        get() = _uiState.asStateFlow()

    private val _user = MutableStateFlow<UserFb?>(null)
    val user: StateFlow<UserFb?> = _user.asStateFlow()

    //Borrar jugadores
    fun deletePlayer(playerId: String, teamId: String){
        viewModelScope.launch {
            playerRepo.deletePlayerFb(playerId)
            withContext(Dispatchers.IO){
                playerRepo.getPlayersByTeamFb(teamId)
            }
        }
    }

    fun setFavouritePlayer(player: PlayerFb) {
        viewModelScope.launch {
            val firestore = FirebaseFirestore.getInstance()
            val playerFb = firestore.collection("players").document(player.id ?: return@launch)
            userRepo.updateUserPlayerFav(playerFb)

            val updatedUser = userRepo.getActualUserFb()
            _user.emit(updatedUser)

            // Opcional: vuelve a emitir la misma lista para que se redibuje
            (_uiState.value as? PlayerListUiState.Success)?.let {
                _uiState.emit(PlayerListUiState.Success(it.playerList.toList()))
            }
        }
    }

    init {

        viewModelScope.launch {
            val actualUser = userRepo.getActualUserFb()
            _user.value = actualUser
        }

        viewModelScope.launch {
            withContext(Dispatchers.IO){
                playerRepo.setStreamFb.collect{
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

    //Poner jugadores en favoritos
    /**fun toggleFavouritePlayers(player: Player, teamId: Int) {
        viewModelScope.launch {
            val updatedPlayer = PlayerUpdate(
                data = PlayerRawAttributesMedia(
                    name = player.name,
                    firstSurname = player.firstSurname,
                    secondSurname = player.secondSurname,
                    position = player.position,
                    birthdate = player.birthdate,
                    nationality = player.nationality,
                    dorsal = player.dorsal,
                    isFavourite = !player.isFavourite,
                    team = null,
                    playerProfilePhoto = null
                    /**No enviamos el logo para que no se modifique**/
                    /**No enviamos el logo para que no se modifique**/

                )
            )
            playerRepo.updatePlayer(player.id.toInt(), updatedPlayer)
            withContext(Dispatchers.IO) {
                playerRepo.readPlayersByTeam(teamId)
            }
        }
    }**/

    //FUNCIÓN leer jugadores por id de equipo
    fun observePlayersByTeam(teamId:String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                playerRepo.getPlayersByTeamFb(teamId)
            }
        }
    }

}

//UISTATE
sealed class PlayerListUiState(){
    data object Loading: PlayerListUiState()
    class Success(val playerList: List<PlayerFb>): PlayerListUiState()
    class Error(val message: String): PlayerListUiState()
}