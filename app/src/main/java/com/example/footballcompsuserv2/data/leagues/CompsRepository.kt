package com.example.footballcompsuserv2.data.leagues

import com.example.footballcompsuserv2.data.remote.leagues.CompCreate
import com.example.footballcompsuserv2.data.remote.leagues.ICompRemoteDataSource
import com.example.footballcompsuserv2.data.teams.toExternal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class CompsRepository @Inject constructor(
    private val remoteData: ICompRemoteDataSource
): ICompsRepository {

    private val _state = MutableStateFlow<List<Competition>>(listOf())
    override val setStream: StateFlow<List<Competition>>
        get() = _state.asStateFlow()

    override suspend fun readAll(): List<Competition> {
        val res = remoteData.readAll()
        val comps = _state.value.toMutableList()
        if (res.isSuccessful){
            val compList = res.body()?.data ?: emptyList()
            _state.value = compList.toExternal()
        }
        else _state.value = comps
        return comps
    }

    override suspend fun readFavs(isFav: Boolean): List<Competition> {
        val filters = mapOf(
            "filters[isFavourite][\$eq]" to isFav
        )
        val res = remoteData.readFavs(filters)
        if (res.isSuccessful) {
            val compList = res.body()?.data ?: emptyList()
            _state.value = compList.toExternal()
            return _state.value.filter { it.isFavourite } // Doble verificación en el cliente
        }
        return emptyList()
    }

    override suspend fun readOne(id: Int): Competition {
        val res = remoteData.readOne(id)
        return if (res.isSuccessful)res.body()!!
        else Competition("0","", "", false)
    }

    override suspend fun createComp(comp: CompCreate) {
        remoteData.createComp(comp)
    }

    override suspend fun deleteComp(id: Int) {
        remoteData.deleteComp(id)
    }

}