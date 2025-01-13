package com.example.footballcompsuserv2.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.footballcompsuserv2.data.leagues.Competition
import com.example.footballcompsuserv2.data.leagues.ICompsRepository
import com.example.footballcompsuserv2.data.remote.leagues.CompCreate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateCompViewModel @Inject constructor(
    private val compRepo: ICompsRepository
): ViewModel() {
    private val _uiState = MutableStateFlow<CreatePlayerUiState>(CreatePlayerUiState.Loading)
    val uiState: StateFlow<CreatePlayerUiState>
        get() = _uiState.asStateFlow()


    fun createComp(comp: CompCreate){
        viewModelScope.launch {
            compRepo.createComp(comp)
        }
    }
}

sealed class CreateCompUiState(){
    data object Loading: CreatePlayerUiState()
    class Success(val comp: List<Competition>): CreatePlayerUiState()
    class Error(val message: String): CreatePlayerUiState()

}