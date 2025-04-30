package com.example.footballcompsuserv2.ui.viewModels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.footballcompsuserv2.data.remote.teams.TeamCreate
import com.example.footballcompsuserv2.data.teams.ITeamRepository
import com.example.footballcompsuserv2.data.teams.Team
import com.example.footballcompsuserv2.data.teams.TeamFbFields
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
class CreateTeamViewModel @Inject constructor(
    private val teamRepo: ITeamRepository
): ViewModel() {
    private val _uiState = MutableStateFlow<CreateTeamUiState>(CreateTeamUiState.Loading)
    val uiState: StateFlow<CreateTeamUiState>
        get() = _uiState.asStateFlow()

    private val _photo = MutableStateFlow<Uri>(Uri.EMPTY)
    val photo: StateFlow<Uri>
        get() = _photo.asStateFlow()

    private suspend fun getCurrentUserRef(): DocumentReference? {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return null
        val firestore = FirebaseFirestore.getInstance()

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

    //FUNCIÓN Crear equipos
    fun createTeam(team: TeamFbFields, logo: Uri?, compId: String){
        viewModelScope.launch {
            val userRef = getCurrentUserRef()
            val teamWithUser = team.copy(userId = userRef)
            teamRepo.createTeamWithOptionalImage(teamWithUser, logo, compId)
        }
    }
}

//UISTATE
sealed class CreateTeamUiState(){
    data object Loading: CreateTeamUiState()
    class Success(val team: List<Team>): CreateTeamUiState()
    class Error(val message: String): CreateTeamUiState()

}