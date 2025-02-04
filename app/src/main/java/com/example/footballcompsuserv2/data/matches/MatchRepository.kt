package com.example.footballcompsuserv2.data.matches

import com.example.footballcompsuserv2.data.remote.matches.IMatchesRemoteDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class MatchRepository @Inject constructor(
    val remote: IMatchesRemoteDataSource
): IMatchRepository{
    private val _state = MutableStateFlow<List<Match>>(listOf())
    override val setStream: StateFlow<List<Match>>
        get() = _state.asStateFlow()

    override suspend fun readAll(): List<Match> {
        val res = remote.readAll()
        val matches = _state.value.toMutableList()

        if (res.isSuccessful){
            val matchList = res.body()?.data ?: emptyList()
            matchList.forEach { match ->
                println("Local ID: ${match.attributes.local}")
                println("Visitor ID: ${match.attributes.visitor}")
            }
            _state.value = matchList.toExternal()
        }
        else _state.value = matches
        return matches
    }
}