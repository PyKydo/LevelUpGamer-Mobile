package cl.duoc.levelupgamer.data.remote.api

import cl.duoc.levelupgamer.data.remote.dto.auth.TokenRefreshRequest
import cl.duoc.levelupgamer.data.remote.dto.auth.TokenRefreshResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LevelUpAuthCallApi {
    @POST("api/auth/refresh")
    fun refreshToken(@Body body: TokenRefreshRequest): Call<TokenRefreshResponse>
}
