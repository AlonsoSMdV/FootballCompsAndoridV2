package com.example.footballcompsuserv2.ui.viewModels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.footballcompsuserv2.data.leagues.CompetitionFb
import com.example.footballcompsuserv2.data.leagues.CompetitionFbCreateUpdate

import com.example.footballcompsuserv2.data.players.IPlayerRepository
import com.example.footballcompsuserv2.data.players.Player
import com.example.footballcompsuserv2.data.players.PlayerFb
import com.example.footballcompsuserv2.data.players.PlayerFbFields
import com.example.footballcompsuserv2.data.remote.players.PlayerCreate
import com.example.footballcompsuserv2.di.Firestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

import javax.inject.Inject

@HiltViewModel
class CreatePlayerViewModel @Inject constructor(
    private val playerRepo: IPlayerRepository
): ViewModel() {
    private val _uiState = MutableStateFlow<CreatePlayerUiState>(CreatePlayerUiState.Loading)
    val uiState: StateFlow<CreatePlayerUiState>
        get() = _uiState.asStateFlow()

    private val _photo = MutableStateFlow<Uri>(Uri.EMPTY)
    val photo: StateFlow<Uri>
        get() = _photo.asStateFlow()

    private suspend fun getCurrentUserRef(): DocumentReference? {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return null
        val firestore = Firestore.getInstance()

        try {
            // Query to find the user document directly by userId
            val querySnapshot = firestore
                .collection("usuarios")
                .whereEqualTo("userId", uid)
                .get()
                .await()

            // If we found a document, return its reference
            if (!querySnapshot.isEmpty) {
                val documentId = querySnapshot.documents.first().id
                return firestore.collection("usuarios").document(documentId)
            }

            // If no document found, log and return null
            Log.d("CreateCompViewModel", "No se encontró documento de usuario con userId = $uid")
            return null
        } catch (e: Exception) {
            Log.e("CreateCompViewModel", "Error getting user reference: ${e.message}")
            return null
        }
    }

    /**
     * Función para poner en el flujo la uri de la ultima foto capturada
     * @param uri [Uri] de la foto que apunta al archivo local
     */
    fun onImageCaptured(uri: Uri?) {
        viewModelScope.launch {
            uri?.let {
                _photo.value = uri
            }
        }
    }

    fun getPlayerById(id: String): PlayerFb? {
        return playerRepo.setStreamFb.value.find { it.id == id }
    }

    //FUNCIÓN Crear jugadores
    fun createPlayer(player: PlayerFbFields, photo: Uri?, team: String){
        viewModelScope.launch {
            val userRef = getCurrentUserRef()
            val playerWithUser = player.copy(userId = userRef)
            playerRepo.createPlayerWithOptionalImage(playerWithUser, photo, team)
        }
    }

    //Actualizar ligas
    fun updatePlayer(playerId: String, playerField: PlayerFbFields, logo: Uri?){
        viewModelScope.launch {
            playerRepo.updatePlayersWithOptionalImage(playerId, playerField, logo)
        }
    }
}

//UISTATE
sealed class CreatePlayerUiState(){
    data object Loading: CreatePlayerUiState()
    class Success(val player: List<PlayerFbFields>): CreatePlayerUiState()
    class Error(val message: String): CreatePlayerUiState()

}