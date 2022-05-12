package ru.tsu.testws.network

import okhttp3.MultipartBody
import ru.tsu.testws.network.auth.AuthForm
import ru.tsu.testws.network.auth.TokenResponse

class ApiRepo(private val api: Api) {

    suspend fun register(form: AuthForm): TokenResponse = api.register(form)

    suspend fun login(form: AuthForm): TokenResponse = api.login(form)

    suspend fun getProfile() = api.getProfile("Bearer ${Network.token!!.accessToken}")

    suspend fun uploadAvatar(avatar: MultipartBody.Part) =
        api.uploadAvatar(
            "Bearer ${Network.token!!.accessToken}",
            avatar
        )

    suspend fun deleteAvatar() = api.deleteAvatar("Bearer ${Network.token!!.accessToken}")

    suspend fun getAvatarUrl(url: String) = api.getProfileAvatar(url)
}