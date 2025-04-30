package com.example.footballcompsuserv2.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.footballcompsuserv2.data.matches.IMatchRepository
import com.example.footballcompsuserv2.data.matches.Match
import com.example.footballcompsuserv2.data.matches.MatchFbWithTeams
import com.example.footballcompsuserv2.data.teams.ITeamRepository
import com.example.footballcompsuserv2.data.teams.Team
import com.example.footballcompsuserv2.data.teams.TeamFb

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import javax.inject.Inject

@HiltViewModel
class MatchesViewModel @Inject constructor(
    private val teamRepo: ITeamRepository,
    private val matchRepo: IMatchRepository
): ViewModel(){
    private val _uiState = MutableStateFlow<MatchUIState>(MatchUIState.Loading)
    val  uiState: StateFlow<MatchUIState>
        get() = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                matchRepo.setStreamFb.collect{
                        matchList ->
                    if (matchList.isEmpty()){
                        _uiState.value = MatchUIState.Loading
                    }else{
                        _uiState.value = MatchUIState.Success(matchList)
                    }
                }
            }
        }
        //Leer los equipos
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                matchRepo.getMatchesFb()
            }
        }
    }

}

sealed class MatchUIState(){
    data object Loading: MatchUIState()
    class Success(val matchList: List<MatchFbWithTeams>): MatchUIState()
    class Error(val message: String): MatchUIState()
}

sealed class TeamUIState(){
    data object Loading: TeamUIState()
    class Success(val team: TeamFb): TeamUIState()
    class Error(val message: String): TeamUIState()
}