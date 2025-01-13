package com.example.footballcompsuserv2.data.players

import com.example.footballcompsuserv2.data.remote.players.IPlayerRemoteDataSource
import com.example.footballcompsuserv2.data.remote.players.PlayerCreate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class PlayerRepository @Inject constructor(
    private val remoteData: IPlayerRemoteDataSource
): IPlayerRepository {

    private val _state = MutableStateFlow<List<Player>>(listOf())
    override val setStream: StateFlow<List<Player>>
        get() = _state.asStateFlow()

    override suspend fun readAll(): List<Player> {
        val res = remoteData.readAll()
        val players = _state.value.toMutableList()
        if (res.isSuccessful){
            val playerList = res.body()?.data ?: emptyList()
            _state.value = playerList.toExternal()
        }
        else _state.value = players
        return players
    }

    override suspend fun readPlayersByTeam(teamId: Int): List<Player> {
        val filters = mapOf(
            "filters[team][id][\$eq]" to teamId.toString()
        )
        val res = remoteData.readPlayersByTeam(filters)
        val players = _state.value.toMutableList()
        if (res.isSuccessful){
            val playerList = res.body()?.data ?: emptyList()
            _state.value = playerList.toExternal()
        }
        else _state.value = players
        return players
    }

    override suspend fun readOne(id: Int): Player {
        val res = remoteData.readOne(id)
        return if (res.isSuccessful)res.body()!!
        else Player("0","","", "", "", "", 0, "0", "")
    }

    override suspend fun createPlayer(player: PlayerCreate) {
        remoteData.createPlayer(player)
    }

    override suspend fun deletePlayer(id: Int) {
        remoteData.deletePlayer(id)
    }

}