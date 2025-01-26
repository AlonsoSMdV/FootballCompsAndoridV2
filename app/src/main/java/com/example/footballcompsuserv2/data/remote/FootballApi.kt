package com.example.footballcompsuserv2.data.remote

import com.example.footballcompsuserv2.data.leagues.Competition
import com.example.footballcompsuserv2.data.players.Player
import com.example.footballcompsuserv2.data.remote.leagues.CompCreate
import com.example.footballcompsuserv2.data.remote.leagues.CompListRaw
import com.example.footballcompsuserv2.data.remote.loginRegister.LoginRaw
import com.example.footballcompsuserv2.data.remote.loginRegister.LoginRegisterResponse
import com.example.footballcompsuserv2.data.remote.loginRegister.RegisterRaw
import com.example.footballcompsuserv2.data.remote.players.PlayerCreate
import com.example.footballcompsuserv2.data.remote.players.PlayerListRaw
import com.example.footballcompsuserv2.data.remote.teams.TeamCreate
import com.example.footballcompsuserv2.data.remote.teams.TeamListRaw
import com.example.footballcompsuserv2.data.teams.Team
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface   FootballApi {
    @GET("leagues/?populate=logo")
    suspend fun getCompetitions(): Response<CompListRaw>
    @GET("leagues/{id}")
    suspend fun getOneCompetition(@Path("id")id: Int): Response<Competition>
    @GET("leagues")
    suspend fun getFavComps(@QueryMap filters: Map<String, Boolean>):Response<CompListRaw>
    @POST("leagues")
    suspend fun createComp(@Body comp: CompCreate)
    @DELETE("leagues/{id}")
    suspend fun deleteComp(@Path("id")id: Int)
    @PUT("leagues/{id}")
    suspend fun updateComp(@Path("id")id: Int, @Body comp: CompCreate)

    @GET("teams")
    suspend fun getTeams(): Response<TeamListRaw>
    @GET("teams")
    suspend fun getFavTeams(@QueryMap filters: Map<String, Boolean>): Response<TeamListRaw>
    @GET("teams/{id}")
    suspend fun getOneTeam(id : Int): Response<Team>
    @GET("teams")
    suspend fun getTeamsByLeague(@QueryMap filters: Map<String, String>): Response<TeamListRaw>
    @POST("teams")
    suspend fun createTeam(@Body team: TeamCreate)
    @DELETE("teams/{id}")
    suspend fun deleteTeam(@Path("id")id: Int)
    @PUT("teams/{id}")
    suspend fun updateTeam(@Path("id")id: Int, @Body team: TeamCreate)

    @GET("players")
    suspend fun getPlayers(): Response<PlayerListRaw>
    @GET("players")
    suspend fun getFavPlayers(@QueryMap filters: Map<String, Boolean>): Response<PlayerListRaw>
    @GET("players/{id}")
    suspend fun getOnePlayer(@Path("id")id : Int): Response<Player>
    @GET("players")
    suspend fun getPlayersByTeam(@QueryMap filters: Map<String, String>): Response<PlayerListRaw>
    @POST("players")
    suspend fun createPlayer(@Body player: PlayerCreate)
    @DELETE("players/{id}")
    suspend fun deletePlayer(@Path("id")id: Int)
    @PUT("players/{id}")
    suspend fun updatePlayer(@Path("id")id: Int, @Body player: PlayerCreate)

    @POST("auth/local")
    suspend fun login(@Body loginUser: LoginRaw): Response<LoginRegisterResponse>

    @POST("auth/local/register")
    suspend fun register(@Body registerUser: RegisterRaw): Response<LoginRegisterResponse>
}