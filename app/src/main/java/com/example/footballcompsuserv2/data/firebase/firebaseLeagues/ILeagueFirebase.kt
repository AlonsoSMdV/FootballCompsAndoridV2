package com.example.footballcompsuserv2.data.firebase.firebaseLeagues

import com.example.footballcompsuserv2.data.leagues.Competition

interface ILeagueFirebase {

    suspend fun getAllLeagues(): List<Competition>
    suspend fun createLeague(league: Competition)
    suspend fun deleteComp()
}