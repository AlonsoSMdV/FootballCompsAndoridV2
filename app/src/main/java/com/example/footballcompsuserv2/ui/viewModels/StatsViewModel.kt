package com.example.footballcompsuserv2.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.footballcompsuserv2.data.matchStatistics.IStatsRepository
import com.example.footballcompsuserv2.data.matchStatistics.Stat
import com.example.footballcompsuserv2.data.matchStatistics.StatsFb
import com.example.footballcompsuserv2.data.matches.IMatchRepository
import com.example.footballcompsuserv2.data.matches.MatchFb
import com.example.footballcompsuserv2.data.matches.MatchFbWithTeams
import com.example.footballcompsuserv2.data.teams.ITeamRepository
import com.example.footballcompsuserv2.data.teams.TeamFb
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val statsRepo: IStatsRepository,
    private val matchRepo: IMatchRepository
) : ViewModel() {

    private val _stats = MutableStateFlow(StatsFb())
    val stats: StateFlow<StatsFb> = _stats.asStateFlow()

    private val _match = MutableStateFlow<MatchFbWithTeams?>(null)
    val match: StateFlow<MatchFbWithTeams?> = _match.asStateFlow()

    private val statNames = listOf(
        "Tiros", "Tiros a puerta", "Posesión", "Pases", "Precisión de pases",
        "Faltas", "Tarjetas amarillas", "Tarjetas rojas", "Fueras de juego", "Saques de esquina"
    )

    fun loadStats(matchId: String, matchStatus: String) {
        viewModelScope.launch {
            val statsFb = when (matchStatus.lowercase()) {
                "por jugar" -> generateZeroStats()
                "jugando" -> generateRandomStats()
                "finalizado" -> statsRepo.getStatsByMatch(matchId)
                else -> StatsFb()
            }
            _stats.value = statsFb
        }
    }

    private fun generateZeroStats(): StatsFb {
        val stats = statNames.map { Stat("0", it, "0") }
        return StatsFb(stats = stats)
    }

    private fun generateRandomStats(): StatsFb {
        val stats = statNames.map {
            Stat(
                localValue = (0..20).random().toString(),
                name = it,
                visitorValue = (0..20).random().toString()
            )
        }
        return StatsFb(stats = stats)
    }

    fun getMatchById(id: String) {
        viewModelScope.launch {
            _match.value = matchRepo.setStreamFb.value.find { it.id == id }
        }
    }
}
