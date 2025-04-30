package com.example.footballcompsuserv2.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.footballcompsuserv2.data.leagues.Competition
import com.example.footballcompsuserv2.data.leagues.CompetitionFb
import com.example.footballcompsuserv2.data.leagues.ICompsRepository
import com.example.footballcompsuserv2.data.remote.leagues.CompRawAttributesMedia
import com.example.footballcompsuserv2.data.remote.leagues.CompUpdate
import com.example.footballcompsuserv2.di.Firestore

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import javax.inject.Inject

@HiltViewModel
class CompetitionViewModel @Inject constructor(
    private val compRepo: ICompsRepository
): ViewModel(){
    private val _uiState = MutableStateFlow<CompListUiState>(CompListUiState.Loading)
    val  uiState: StateFlow<CompListUiState>
        get() = _uiState.asStateFlow()

    fun deleteComp(comId: String){
        viewModelScope.launch {
            compRepo.deleteLeagueFb(comId)
            withContext(Dispatchers.IO){
                compRepo.getLeaguesFb()
            }
        }
    }

    /**fun toggleFavourite(competition: Competition) {
        viewModelScope.launch {
            val updatedCompetition = CompUpdate(
                data = CompRawAttributesMedia(
                    name = competition.name,
                    isFavourite = !competition.isFavourite,
                    logo = null // No enviamos el logo para que no se modifique
                )
            )
            compRepo.updateComp(competition.id.toInt(), updatedCompetition)
            withContext(Dispatchers.IO) {
                compRepo.readAll()
            }
        }
    }**/

    init {
        viewModelScope.launch {
            compRepo.getLeaguesFb()
        }

        viewModelScope.launch {
            withContext(Dispatchers.IO){
                compRepo.setStreamFb.collect{
                    compList ->
                    if (compList.isEmpty()){
                        _uiState.value = CompListUiState.Loading
                    }else{
                        _uiState.value = CompListUiState.Success(compList)
                    }
                }
            }
        }

    }

}

sealed class CompListUiState(){
    data object Loading: CompListUiState()
    class Success(val compList: List<CompetitionFb>): CompListUiState()
    class Error(val message: String): CompListUiState()
}