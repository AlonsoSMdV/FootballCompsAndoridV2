package com.example.footballcompsuserv2.ui.viewModels

import android.net.Uri
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
    private val _uiState = MutableStateFlow<CreateCompUiState>(CreateCompUiState.Loading)
    val uiState: StateFlow<CreateCompUiState>
        get() = _uiState.asStateFlow()

    private val _photo = MutableStateFlow<Uri>(Uri.EMPTY)
    val photo: StateFlow<Uri>
        get() = _photo.asStateFlow()

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

    //Crear ligas
    fun createComp(comp: CompCreate, logo: Uri?){
        viewModelScope.launch {
            compRepo.createComp(comp, logo)
        }
    }
}

//UISTATE
sealed class CreateCompUiState(){
    data object Loading: CreateCompUiState()
    class Success(val comp: List<Competition>): CreateCompUiState()
    class Error(val message: String): CreateCompUiState()

}