package com.example.footballcompsuserv2.ui.viewModels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.footballcompsuserv2.data.leagues.Competition
import com.example.footballcompsuserv2.data.leagues.CompetitionFb
import com.example.footballcompsuserv2.data.leagues.CompetitionFbCreateUpdate
import com.example.footballcompsuserv2.data.leagues.ICompsRepository
import com.example.footballcompsuserv2.data.remote.leagues.CompCreate
import com.example.footballcompsuserv2.data.user.IUserRepository
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
class CreateCompViewModel @Inject constructor(
    private val compRepo: ICompsRepository,
    private val userRepo: IUserRepository
): ViewModel() {
    private val _uiState = MutableStateFlow<CreateCompUiState>(CreateCompUiState.Loading)
    val uiState: StateFlow<CreateCompUiState>
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
    fun onImageCaptured(uri:Uri?) {
        viewModelScope.launch {
            uri?.let {
                _photo.value = uri
            }
        }

    }

    fun getCompetitionById(id: String): CompetitionFb? {
        return compRepo.setStreamFb.value.find { it.id == id }
    }

    //Crear ligas
    fun createComp(comp: CompetitionFbCreateUpdate, logo: Uri?){
        viewModelScope.launch {
            val userRef = getCurrentUserRef()
            val compWithUser = comp.copy(userId = userRef)
            compRepo.createLeagueWithOptionalImage(compWithUser, logo)
        }
    }

    //Actualizar ligas
    fun updateComp(compId: String, compfield: CompetitionFbCreateUpdate, logo: Uri?){
        viewModelScope.launch {
            compRepo.updateLeagueWithOptionalImage(compId, compfield, logo)
        }
    }
}

//UISTATE
sealed class CreateCompUiState(){
    data object Loading: CreateCompUiState()
    class Success(val comp: List<Competition>): CreateCompUiState()
    class Error(val message: String): CreateCompUiState()

}