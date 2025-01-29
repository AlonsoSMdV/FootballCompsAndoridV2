package com.example.footballcompsuserv2.data.user

import com.example.footballcompsuserv2.data.remote.user.UserRemoteDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val remoteDS: UserRemoteDataSource
): IUserRepository{

    private val _state = MutableStateFlow<List<User>>(listOf())
    override val setStream: StateFlow<List<User>>
        get() = _state.asStateFlow()

    override suspend fun getActualUser(): User {
        val res = remoteDS.getActualUser()
        return if (res.isSuccessful){
                val user = res.body()?.toExternal() ?: User("", "", "")
            user
        }else{
            User("", "", "")
        }
    }

}