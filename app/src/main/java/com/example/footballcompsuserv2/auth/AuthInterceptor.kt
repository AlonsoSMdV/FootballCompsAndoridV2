package com.example.footballcompsuserv2.auth

import kotlinx.coroutines.runBlocking

import okhttp3.Interceptor
import okhttp3.Response

import javax.inject.Inject

//Interceptor de token para comprobar el token y si tiene lo manda con un Authentication: Bearer tkn
class AuthInterceptor @Inject constructor(
    private val authSvc: AuthService
): Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        if (chain.request().method == "POST" && (chain.request().url.encodedPath == "api/auth/local" ||
                chain.request().url.encodedPath == "api/auth/local/register")){
            return chain.proceed(chain.request())
        }

        val tkn: String? =
            runBlocking {
                authSvc.getToken()?.let {
                    return@runBlocking it
                }
                    return@runBlocking null
            }

        tkn?.let {
            val newRequest = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $it")
                .build()
            return chain.proceed(newRequest)
        }
        return chain.proceed(chain.request())
    }
}