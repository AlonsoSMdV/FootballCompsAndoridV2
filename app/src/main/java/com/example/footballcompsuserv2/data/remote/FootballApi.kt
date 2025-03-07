package com.example.footballcompsuserv2.data.remote

import com.example.footballcompsuserv2.data.leagues.Competition
import com.example.footballcompsuserv2.data.remote.leagues.CompCreate
import com.example.footballcompsuserv2.data.remote.leagues.CompListRaw
import com.example.footballcompsuserv2.data.remote.leagues.CompRaw
import com.example.footballcompsuserv2.data.remote.leagues.CompUpdate
import com.example.footballcompsuserv2.data.remote.loginRegister.LoginRaw
import com.example.footballcompsuserv2.data.remote.loginRegister.LoginRegisterResponse
import com.example.footballcompsuserv2.data.remote.loginRegister.RegisterRaw
import com.example.footballcompsuserv2.data.remote.matches.MatchesListRaw
import com.example.footballcompsuserv2.data.remote.players.PlayerCreate
import com.example.footballcompsuserv2.data.remote.players.PlayerListRaw
import com.example.footballcompsuserv2.data.remote.players.PlayerRaw
import com.example.footballcompsuserv2.data.remote.players.PlayerResponse
import com.example.footballcompsuserv2.data.remote.players.PlayerUpdate
import com.example.footballcompsuserv2.data.remote.teams.TeamCreate
import com.example.footballcompsuserv2.data.remote.teams.TeamListRaw
import com.example.footballcompsuserv2.data.remote.teams.TeamRaw
import com.example.footballcompsuserv2.data.remote.teams.TeamResponse
import com.example.footballcompsuserv2.data.remote.teams.TeamUpdate
import com.example.footballcompsuserv2.data.remote.uploadImg.CreatedMediaItemResponse
import com.example.footballcompsuserv2.data.remote.uploadImg.StrapiResponse
import com.example.footballcompsuserv2.data.remote.user.UserRaw

import okhttp3.MultipartBody
import okhttp3.RequestBody

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.QueryMap

//Interfaz on funciones para traer, añadir, borrar, o modificar datos de strapi
interface   FootballApi {
    //LIGAS
    @GET("leagues/?populate=logo")
    suspend fun getCompetitions(): Response<CompListRaw>
    @GET("leagues/{id}")
    suspend fun getOneCompetition(@Path("id")id: Int): Response<Competition>
    @GET("leagues?populate=logo&")
    suspend fun getFavComps(@QueryMap filters: Map<String, Boolean>):Response<CompListRaw>
    @POST("leagues")
    suspend fun createComp(@Body comp: CompCreate): Response<StrapiResponse<CompRaw>>
    @DELETE("leagues/{id}")
    suspend fun deleteComp(@Path("id")id: Int)
    @PUT("leagues/{id}")
    suspend fun updateComp(@Path("id")id: Int, @Body comp: CompUpdate)

    //EQUIPOS
    @GET("teams")
    suspend fun getTeams(): Response<TeamListRaw>
    @GET("teams?populate=teamLogo&")
    suspend fun getFavTeams(@QueryMap filters: Map<String, Boolean>): Response<TeamListRaw>
    @GET("teams/{id}?populate=teamLogo")
    suspend fun getOneTeam(id : Int): Response<TeamResponse>
    @GET("teams?populate=teamLogo,league.id&")
    suspend fun getTeamsByLeague(@QueryMap filters: Map<String, String>): Response<TeamListRaw>
    @POST("teams")
    suspend fun createTeam(@Body team: TeamCreate): Response<StrapiResponse<TeamRaw>>
    @DELETE("teams/{id}")
    suspend fun deleteTeam(@Path("id")id: Int)
    @PUT("teams/{id}")
    suspend fun updateTeam(@Path("id")id: Int, @Body team: TeamUpdate)

    //JUGADORES
    @GET("players")
    suspend fun getPlayers(): Response<PlayerListRaw>
    @GET("players?populate=playerProfilePhoto&")
    suspend fun getFavPlayers(@QueryMap filters: Map<String, Boolean>): Response<PlayerListRaw>
    @GET("players/{id}?populate=playerProfilePhoto")
    suspend fun getOnePlayer(@Path("id")id : Int): Response<PlayerResponse>
    @GET("players?populate=playerProfilePhoto,team.id&")
    suspend fun getPlayersByTeam(@QueryMap filters: Map<String, String>): Response<PlayerListRaw>
    @POST("players")
    suspend fun createPlayer(@Body player: PlayerCreate): Response<StrapiResponse<PlayerRaw>>
    @DELETE("players/{id}")
    suspend fun deletePlayer(@Path("id")id: Int)
    @PUT("players/{id}")
    suspend fun updatePlayer(@Path("id")id: Int, @Body player: PlayerUpdate)

    //PARTIDOS
    @GET("matches?populate=local.teamLogo,visitor.teamLogo")
    suspend fun getMatches():Response<MatchesListRaw>

    //USUARIOS
    @GET("users/me")
    suspend fun getActualUser(): Response<UserRaw>

    @POST("auth/local")
    suspend fun login(@Body loginUser: LoginRaw): Response<LoginRegisterResponse>

    @POST("auth/local/register")
    suspend fun register(@Body registerUser: RegisterRaw): Response<LoginRegisterResponse>

    //IMAGENES
    @Multipart
    @POST("upload")
    suspend fun addPhoto(@PartMap partMap: MutableMap<String, RequestBody>,
                            @Part files: MultipartBody.Part): Response<List<CreatedMediaItemResponse>>
}