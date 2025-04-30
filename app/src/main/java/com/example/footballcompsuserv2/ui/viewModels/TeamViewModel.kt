package com.example.footballcompsuserv2.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.footballcompsuserv2.data.remote.teams.TeamRawAttributesMedia
import com.example.footballcompsuserv2.data.remote.teams.TeamUpdate
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
class TeamViewModel @Inject constructor(
    private val teamRepo: ITeamRepository
): ViewModel(){
    private val _uiState = MutableStateFlow<TeamListUiState>(TeamListUiState.Loading)
    val  uiState: StateFlow<TeamListUiState>
        get() = _uiState.asStateFlow()

    //Borrar equipos
    fun deleteTeam(teamId: String, leagueId: String){
        viewModelScope.launch {
            teamRepo.deleteTeamFb(teamId)
            withContext(Dispatchers.IO){
                teamRepo.getTeamsByleagueFb(leagueId)
            }
        }
    }

    //Poner equipos en favoritos
    /**fun toggleFavouriteTeams(team: Team, leagueId: String) {
        viewModelScope.launch {
            val updatedTeam = TeamUpdate(
                data = TeamRawAttributesMedia(
                    name = team.name,
                    numberOfPlayers = team.nPlayers,
                    league = null,
                    isFavourite = !team.isFavourite,
                    teamLogo = null
                    /**No enviamos el logo para que no se modifique**/ /**No enviamos el logo para que no se modifique**/

                )
            )
            teamRepo.updateTeam(team.id.toInt(), updatedTeam)
            withContext(Dispatchers.IO) {
                teamRepo.getTeamsByleagueFb(leagueId)
            }
        }
    }**/

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                teamRepo.setStreamFb.collect{
                        teamList ->
                    if (teamList.isEmpty()){
                        _uiState.value = TeamListUiState.Loading
                    }else{
                        _uiState.value = TeamListUiState.Success(teamList)
                    }
                }
            }
        }
        /*viewModelScope.launch {
            withContext(Dispatchers.IO){
                teamRepo.readAll()
            }
        }*/

    }

    //FUNCIÓN Leer equipos por id de liga
    fun observeTeamsByLeague(leagueId:String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                teamRepo.getTeamsByleagueFb(leagueId)
            }
        }
    }

}

//UISTATE
sealed class TeamListUiState(){
    data object Loading: TeamListUiState()
    class Success(val teamList: List<TeamFb>): TeamListUiState()
    class Error(val message: String): TeamListUiState()
}