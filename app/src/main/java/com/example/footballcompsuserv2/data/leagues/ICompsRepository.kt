package com.example.footballcompsuserv2.data.leagues

import android.net.Uri

import com.example.footballcompsuserv2.data.remote.leagues.CompCreate
import com.example.footballcompsuserv2.data.remote.leagues.CompRaw
import com.example.footballcompsuserv2.data.remote.leagues.CompUpdate
import com.example.footballcompsuserv2.data.remote.uploadImg.StrapiResponse

import kotlinx.coroutines.flow.StateFlow

import retrofit2.Response

interface ICompsRepository {
    val setStream: StateFlow<List<Competition>>
    val setStreamFb: StateFlow<List<CompetitionFb>>
    suspend fun readAll(): List<Competition>
    suspend fun readFavs(isFav: Boolean):List<Competition>
    suspend fun readOne(id: Int): Competition
    suspend fun createComp(comp: CompCreate, logo: Uri?): Response<StrapiResponse<CompRaw>>
    suspend fun deleteComp(id: Int)
    suspend fun updateComp(id: Int, comp: CompUpdate)
    suspend fun uploadLeagueLogo(uri: Uri, exerciseId: Int): Result<Uri>
    // Firebase
    suspend fun getLeaguesFb(): List<CompetitionFb>
    suspend fun addLeagueFb(competition: CompetitionFbCreateUpdate): Boolean
    suspend fun updateLeagueFb(compId: String, competition: CompetitionFbCreateUpdate): Boolean
    suspend fun deleteLeagueFb(id: String): Boolean
    suspend fun uploadImageToFirebaseStorage(uri: Uri): String?
    suspend fun createLeagueWithOptionalImage(competition: CompetitionFbCreateUpdate, imageUri: Uri?): Boolean
    suspend fun updateLeagueWithOptionalImage(leagueId: String, updatedData: CompetitionFbCreateUpdate, imageUri: Uri?): Boolean
}