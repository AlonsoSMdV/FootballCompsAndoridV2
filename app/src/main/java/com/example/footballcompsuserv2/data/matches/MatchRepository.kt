package com.example.footballcompsuserv2.data.matches

import com.example.footballcompsuserv2.data.local.ILocalDataSource
import com.example.footballcompsuserv2.data.remote.matches.IMatchesRemoteDataSource
import com.example.footballcompsuserv2.data.teams.TeamFb
import com.example.footballcompsuserv2.di.NetworkUtils
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

import javax.inject.Inject

//Clase que obtiene del partido ya sea por remoto o local(si no hay red)
class MatchRepository @Inject constructor(
    private val remote: IMatchesRemoteDataSource,
    private val local: ILocalDataSource, // Agregamos la fuente de datos local
    private val networkUtils: NetworkUtils
) : IMatchRepository {

    private val _state = MutableStateFlow<List<Match>>(listOf())
    override val setStream: StateFlow<List<Match>>
        get() = _state.asStateFlow()

    private val _stateFb = MutableStateFlow<List<MatchFbWithTeams>>(listOf())
    override val setStreamFb: StateFlow<List<MatchFbWithTeams>>
        get() = _stateFb.asStateFlow()

    //OBTENER todos los datos
    override suspend fun readAll(): List<Match> {
        val matches = mutableListOf<Match>()
        if (networkUtils.isNetworkAvailable()) {
            //  Si hay internet, obtener datos del servidor
            val res = remote.readAll()
            if (res.isSuccessful) {
                val matchList = res.body()?.data ?: emptyList()
                _state.value = matchList.toExternal()

                //  Guardar los datos en la base de datos local para uso offline
                matchList.forEach { match ->
                    local.createLocalMatch(match.toExternal().toLocal())
                }

                return matchList.toExternal()
            }
        }
        //  Si no hay internet, cargar datos locales
        local.getLocalMatches().collect { localMatches ->
            _state.value = localMatches.localToExternal()
        }

        return matches
    }

    suspend fun getTeamByRef(teamRef: DocumentReference): TeamFb? {
        return try {
            val snapshot = teamRef.get().await()
            snapshot.toObject(TeamFb::class.java)
        } catch (e: Exception) {
            null
        }
    }


    private val firestore = FirebaseFirestore.getInstance()
    override suspend fun getMatchesFb(): List<MatchFbWithTeams> {
        return try {
            val snapshot = firestore.collection("matches").get().await()
            val matchList = mutableListOf<MatchFbWithTeams>()

            for (doc in snapshot.documents) {
                val match = doc.toObject(MatchFb::class.java)

                if (match != null && match.localTeamId != null && match.visitorTeamId != null) {
                    val localTeam = getTeamByRef(match.localTeamId)
                    val visitorTeam = getTeamByRef(match.visitorTeamId)

                    val matchWithTeams = MatchFbWithTeams(
                        day = match.day,
                        hour = match.hour,
                        place = match.place,
                        result = match.result,
                        status = match.status,
                        localTeamId = match.localTeamId,
                        visitorTeamId = match.visitorTeamId,
                        localTeamName = localTeam?.name,
                        visitorTeamName = visitorTeam?.name,
                        localTeamImg = localTeam?.picture,
                        visitorTeamImg = visitorTeam?.picture
                    )

                    matchList.add(matchWithTeams)
                }
            }

            _stateFb.value = matchList
            matchList
        } catch (e: Exception) {
            emptyList()
        }
    }
}