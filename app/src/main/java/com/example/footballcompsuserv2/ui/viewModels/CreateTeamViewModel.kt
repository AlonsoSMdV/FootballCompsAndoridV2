package com.example.footballcompsuserv2.ui.viewModels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.footballcompsuserv2.data.remote.teams.TeamCreate
import com.example.footballcompsuserv2.data.teams.ITeamRepository
import com.example.footballcompsuserv2.data.teams.Team
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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

    fun createTeam(team: TeamCreate){
        viewModelScope.launch {
            teamRepo.createTeam(team)
        }
    }
}

sealed class CreateTeamUiState(){
    data object Loading: CreateTeamUiState()
    class Success(val team: List<Team>): CreateTeamUiState()
    class Error(val message: String): CreateTeamUiState()

}