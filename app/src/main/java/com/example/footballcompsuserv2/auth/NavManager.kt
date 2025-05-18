package com.example.footballcompsuserv2.auth

import com.example.footballcompsuserv2.R

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavManager @Inject constructor(
    private  val authSvc: AuthService
){
    //Fragmentos a los uqe hace falta autenticacion para poder navegar
    private val authDest: Set<Int> = setOf(
        R.id.compsFragment,
        R.id.teamsFragment,
        R.id.playersFragment,
        R.id.playerDetails,
        R.id.profileDetails,
        R.id.createCompsFragment,
        R.id.createTeamFragment,
        R.id.createPlayerFragment,
        R.id.favouritesFragment,
        R.id.statsFragment,
        R.id.lineupsFragment,
        R.id.matchesFragment,
        R.id.mapsFragment,
        R.id.cameraPreviewFragment
    )

    fun navsToLogin(destId: Int): Boolean{
        return authDest.contains(destId) && !authSvc.isAuthenticated()
    }
}