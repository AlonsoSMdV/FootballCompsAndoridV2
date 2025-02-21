package com.example.footballcompsuserv2.data.remote.user

import com.example.footballcompsuserv2.data.remote.FootballApi

import retrofit2.Response

import javax.inject.Inject

//Clase a la que se le pasa de constructor la interfaz de strapi
//Tiene la función para obetner al usuario actualmente logeado
class UserRemoteDataSource @Inject constructor(
    private val userApi: FootballApi
): IUserRemoteDataSource{
    override suspend fun getActualUser(): Response<UserRaw> {
        return userApi.getActualUser()
    }
}