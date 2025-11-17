package cl.duoc.levelupgamer.data.remote.network

import cl.duoc.levelupgamer.data.remote.api.LevelUpAuthCallApi
import cl.duoc.levelupgamer.data.remote.dto.auth.TokenRefreshRequest
import cl.duoc.levelupgamer.data.session.TokenStore
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val tokenStore: TokenStore,
    private val refreshApi: LevelUpAuthCallApi
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= MAX_RETRY_COUNT) return null

        val previousSession = tokenStore.currentSession()
        val currentAccessToken = previousSession.accessToken ?: return null
        val authHeader = response.request.header("Authorization") ?: return null
        if (!authHeader.endsWith(currentAccessToken)) {
            return null
        }

        val refreshToken = previousSession.refreshToken ?: return null
        val refreshCall = refreshApi.refreshToken(TokenRefreshRequest(refreshToken))
        val refreshResponse = try {
            refreshCall.execute()
        } catch (_: Exception) {
            null
        } ?: return null

        if (!refreshResponse.isSuccessful) {
            runBlocking { tokenStore.clear() }
            return null
        }

        val body = refreshResponse.body() ?: return null

        runBlocking {
            tokenStore.persistSession(
                previousSession.copy(
                    accessToken = body.accessToken,
                    refreshToken = body.refreshToken,
                    role = body.rol ?: previousSession.role
                )
            )
        }

        return response.request.newBuilder()
            .header("Authorization", "Bearer ${body.accessToken}")
            .build()
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var current: Response? = response.priorResponse
        while (current != null) {
            count++
            current = current.priorResponse
        }
        return count
    }

    private companion object {
        const val MAX_RETRY_COUNT = 2
    }
}
