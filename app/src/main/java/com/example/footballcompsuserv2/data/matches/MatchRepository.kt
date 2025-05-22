package com.example.footballcompsuserv2.data.matches

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.footballcompsuserv2.data.local.ILocalDataSource
import com.example.footballcompsuserv2.data.matchStatistics.Stat
import com.example.footballcompsuserv2.data.matchStatistics.StatsFbFields
import com.example.footballcompsuserv2.data.remote.matches.IMatchesRemoteDataSource
import com.example.footballcompsuserv2.data.teams.TeamFb
import com.example.footballcompsuserv2.di.Firestore
import com.example.footballcompsuserv2.di.NetworkUtils
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

import javax.inject.Inject
import kotlin.random.Random

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


    private val firestore = Firestore.getInstance()
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
                        id = doc.id,
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

    override suspend fun getMatchesFbById(id: String): MatchFbWithTeams {
        return try {
            val doc = firestore.collection("matches").document(id).get().await()
            val match = doc.toObject(MatchFb::class.java)

            if (match?.localTeamId != null && match.visitorTeamId != null) {
                val localTeam = getTeamByRef(match.localTeamId)
                val visitorTeam = getTeamByRef(match.visitorTeamId)

                MatchFbWithTeams(
                    id = doc.id,
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
            } else {
                MatchFbWithTeams("","","","","","",null,null,"","","","") // o lanza una excepción si prefieres controlar el error
            }
        } catch (e: Exception) {
            MatchFbWithTeams("","","","","","",null,null,"","","","") // o maneja el error con logs / fallback
        }
    }

    private val statNames = listOf(
        "Tiros", "Tiros a puerta", "Posesión", "Pases", "Precisión de pases",
        "Faltas", "Tarjetas amarillas", "Tarjetas rojas", "Fueras de juego", "Saques de esquina"
    )

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun updateMatchStatuses() {
        val matches = getMatchesFb()

        val formatterDate = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
        val formatterTime = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
        val now = LocalDateTime.now()

        val firestore = Firestore.getInstance()
        val statsCollection = firestore.collection("matchStatistics")
        val matchesCollection = firestore.collection("matches")

        for (match in matches) {
            try {
                val matchDate = match.day?.let { LocalDate.parse(it, formatterDate) }
                val matchTime = match.hour?.let { LocalTime.parse(it, formatterTime) }

                if (matchDate != null && matchTime != null) {
                    val matchDateTime = LocalDateTime.of(matchDate, matchTime)

                    val newStatus = when {
                        now.isBefore(matchDateTime) -> "Por jugar"
                        now.isAfter(matchDateTime.plusHours(2)) -> "Finalizado"
                        now.isAfter(matchDateTime) -> "Jugando"
                        else -> match.status
                    }

                    var newResult = ""

                    if (newStatus == "Finalizado"){
                        val localGoals = (0..5).random()
                        val  visitorGoals = (0..5).random()
                        newResult = "$localGoals - $visitorGoals"
                    }

                    // Actualizar estado si cambió
                    if (newStatus != match.status) {
                        match.id?.let { matchesCollection.document(it).update("status", newStatus, "result", newResult).await() }
                        Log.d("MatchStatusUpdate", "Estado actualizado: ${match.id} -> $newStatus")
                    }

                    // Crear documento de estadísticas si el partido finalizó
                    if (newStatus == "Finalizado") {
                        val matchRef = match.id?.let { matchesCollection.document(it) }

                        // Verificar si ya existe
                        val statsSnapshot = statsCollection
                            .whereEqualTo("matchId", matchRef)
                            .get()
                            .await()

                        if (statsSnapshot.isEmpty) {
                            val possessionLocal = (30..70).random()
                            val possessionVisitor = 100 - possessionLocal

                            val generatedStats = statNames.map { statName ->
                                when (statName) {
                                    "Posesión" -> Stat(
                                        name = statName,
                                        localValue = "$possessionLocal%",
                                        visitorValue = "$possessionVisitor%"
                                    )
                                    else -> Stat(
                                        name = statName,
                                        localValue = generateRandomValueForStat(statName),
                                        visitorValue = generateRandomValueForStat(statName)
                                    )
                                }
                            }


                            val matchStats = StatsFbFields(
                                matchId = matchRef,
                                stats = generatedStats
                            )

                            statsCollection.add(matchStats).await()
                            Log.d("MatchStats", "Estadísticas generadas para partido: ${match.id}")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("MatchStatusUpdate", "Error con partido ${match.id}", e)
            }
        }
    }

    fun generateRandomValueForStat(statName: String): Any {
        return when (statName) {
            "Tiros" -> (5..20).random()
            "Tiros a puerta" -> (2..15).random()
            "Pases" -> (200..800).random()
            "Precisión de pases" -> "${(70..95).random()}%"
            "Faltas" -> (5..20).random()
            "Tarjetas amarillas" -> (0..5).random()
            "Tarjetas rojas" -> (0..2).random()
            "Fueras de juego" -> (0..5).random()
            "Saques de esquina" -> (0..10).random()
            else -> "-"
        }
    }


}