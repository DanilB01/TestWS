package ru.tsu.testws.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import ru.tsu.testws.network.auth.AuthForm
import ru.tsu.testws.network.auth.TokenResponse
import ru.tsu.testws.network.profile.ProfileResponse

interface Api {

    @POST("v1/auth/register")
    suspend fun register(@Body form: AuthForm): TokenResponse

    @POST("v1/auth/login")
    suspend fun login(@Body form: AuthForm): TokenResponse

    @GET("v1/profile")
    suspend fun getProfile(@Header("Authorization") token: String): ProfileResponse

    @Multipart
    @POST("v1/profile/avatar")
    suspend fun uploadAvatar(
        @Header("Authorization") token: String,
        @Part avatar: MultipartBody.Part
    ): Response<Unit>

    @DELETE("v1/profile/avatar")
    suspend fun deleteAvatar(
        @Header("Authorization") token: String
    ): Response<Unit>
}