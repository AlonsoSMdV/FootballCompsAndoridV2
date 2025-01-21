package com.example.footballcompsuserv2.data.leagues

import com.example.footballcompsuserv2.data.remote.leagues.CompCreate
import kotlinx.coroutines.flow.StateFlow

interface ICompsRepository {
    val setStream: StateFlow<List<Competition>>
    suspend fun readAll(): List<Competition>
    suspend fun readFavs(isFav: Boolean):List<Competition>
    suspend fun readOne(id: Int): Competition
    suspend fun createComp(comp: CompCreate)
    suspend fun deleteComp(id: Int)
    suspend fun updateComp(id: Int, comp: CompCreate)
}