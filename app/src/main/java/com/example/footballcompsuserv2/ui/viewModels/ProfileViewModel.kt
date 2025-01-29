package com.example.footballcompsuserv2.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.footballcompsuserv2.data.user.IUserRepository
import com.example.footballcompsuserv2.data.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepo: IUserRepository
): ViewModel(){
    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    fun getActualUser(){
        viewModelScope.launch {
            val userData = userRepo.getActualUser()
            _user.value = userData
        }
    }
}