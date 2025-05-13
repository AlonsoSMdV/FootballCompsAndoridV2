package com.example.footballcompsuserv2.ui.viewModels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.footballcompsuserv2.data.leagues.CompetitionFb
import com.example.footballcompsuserv2.data.players.PlayerFb
import com.example.footballcompsuserv2.data.teams.TeamFb

import com.example.footballcompsuserv2.data.user.IUserRepository
import com.example.footballcompsuserv2.data.user.User
import com.example.footballcompsuserv2.data.user.UserFb

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepo: IUserRepository
): ViewModel(){
    private val _userFb = MutableStateFlow<UserFb?>(null)
    val userFb = _userFb.asStateFlow()


    private val _favTeam = MutableStateFlow<TeamFb?>(null)
    val favTeam = _favTeam.asStateFlow()

    private val _favPlayer = MutableStateFlow<PlayerFb?>(null)
    val favPlayer = _favPlayer.asStateFlow()

    private val _favLeague = MutableStateFlow<CompetitionFb?>(null)
    val favLeague = _favLeague.asStateFlow()

    private val _photo = MutableStateFlow<Uri>(Uri.EMPTY)
    val photo: StateFlow<Uri>
        get() = _photo.asStateFlow()


    /**
     * Función para poner en el flujo la uri de la ultima foto capturada
     * @param uri [Uri] de la foto que apunta al archivo local
     */
    fun onImageCaptured(uri: Uri?) {
        viewModelScope.launch {
            uri?.let {
                Log.d("ProfileViewModel", "Nueva URI capturada: $uri") // <-- Agrega esto
                _photo.value = uri
            }
        }
    }

    fun getActualUserFb(){
        viewModelScope.launch {
            val userData = userRepo.getActualUserFb()
            Log.d("ProfileViewModel", "Usuario recuperado: $userData")
            _userFb.value = userData
            fetchFavorites(userData)
        }
    }


    fun fetchFavorites(userFb: UserFb) {
        viewModelScope.launch {
            _favTeam.value = userRepo.getFavoriteTeam(userFb.teamFav)
            _favPlayer.value = userRepo.getFavoritePlayer(userFb.playerFav)
            _favLeague.value = userRepo.getFavoriteLeague(userFb.leagueFav)
        }
    }

    suspend fun updateUserWithImage(updatedUser: UserFb, imageUri: Uri?): Boolean {
        val userId = updatedUser.userId ?: return false
        return userRepo.updateUserWithOptionalImage(userId, updatedUser, imageUri)
    }
}