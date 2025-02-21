package com.example.footballcompsuserv2.di

import com.example.footballcompsuserv2.auth.AuthInterceptor
import com.example.footballcompsuserv2.auth.AuthService
import com.example.footballcompsuserv2.data.remote.FootballApi

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import javax.inject.Qualifier
import javax.inject.Singleton

//Providers del remoto
@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    companion object {
        const val STRAPI = "https://footballcompsstrapiv3.onrender.com/api/"
    }
    /**
     * Anotación para calificar el interceptor de autenticación
     */
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class AuthInterceptorOkHttpClient

    @Provides
    @AuthInterceptorOkHttpClient
    fun provideAutenticationInterceptor(authentication: AuthService): Interceptor {
        return AuthInterceptor(authentication)
    }

    @Provides
    @Singleton
    fun provideHttpClient(@AuthInterceptorOkHttpClient interceptor: Interceptor):OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient()
            .newBuilder()
            .addInterceptor(interceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(STRAPI)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideStrapiService(retrofit: Retrofit): FootballApi {
        return retrofit.create(FootballApi::class.java)
    }
}