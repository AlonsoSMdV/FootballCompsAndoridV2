package com.example.footballcompsuserv2.ui.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.footballcompsuserv2.data.user.IUserRepository
import com.example.footballcompsuserv2.data.user.User
import com.example.footballcompsuserv2.data.user.UserFb

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepo: IUserRepository
): ViewModel(){
    private val _userFb = MutableStateFlow<UserFb?>(null)
    val userFb = _userFb.asStateFlow()

    fun getActualUserFb(){
        viewModelScope.launch {
            val userData = userRepo.getActualUserFb()
            Log.d("ProfileViewModel", "Usuario recuperado: $userData")
            _userFb.value = userData
        }
    }
}