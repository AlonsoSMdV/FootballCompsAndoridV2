package com.example.footballcompsuserv2.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.footballcompsuserv2.data.leagues.CompetitionFb
import com.example.footballcompsuserv2.data.matches.IMatchRepository
import com.example.footballcompsuserv2.data.matches.MatchFbWithTeams
import com.example.footballcompsuserv2.data.players.IPlayerRepository
import com.example.footballcompsuserv2.data.players.PlayerFb
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LineupsViewModel @Inject constructor(
    private val matchRepo: IMatchRepository,
    private val playerRepo: IPlayerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LineupsUiState>(LineupsUiState.Loading)
    val uiState: StateFlow<LineupsUiState> = _uiState

    fun loadLineups(matchId: String) {
        viewModelScope.launch {
            _uiState.value = LineupsUiState.Loading
            try {
                val match = matchRepo.getMatchesFbById(matchId)

                val localPlayers = match.localTeamId?.id?.let {
                    playerRepo.getPlayersByTeamFb(it)
                } ?: emptyList()

                val visitorPlayers = match.visitorTeamId?.id?.let {
                    playerRepo.getPlayersByTeamFb(it)
                } ?: emptyList()

                _uiState.value = LineupsUiState.Success(
                    match = match,
                    localPlayers = localPlayers,
                    visitorPlayers = visitorPlayers
                )
            } catch (e: Exception) {
                _uiState.value = LineupsUiState.Error(e.message ?: "Error inesperado")
            }
        }
    }
}

sealed class LineupsUiState {
    data object Loading : LineupsUiState()
    data class Success(
        val match: MatchFbWithTeams,
        val localPlayers: List<PlayerFb>,
        val visitorPlayers: List<PlayerFb>
    ) : LineupsUiState()
    data class Error(val message: String) : LineupsUiState()
}
