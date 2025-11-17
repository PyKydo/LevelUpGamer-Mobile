package cl.duoc.levelupgamer.data.remote.network

import cl.duoc.levelupgamer.data.session.TokenStore
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenStore: TokenStore
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val session = tokenStore.currentSession()
        val currentToken = session.accessToken
        val request = if (currentToken.isNullOrBlank()) {
            chain.request()
        } else {
            chain.request().newBuilder()
                .header("Authorization", "Bearer $currentToken")
                .build()
        }
        return chain.proceed(request)
    }
}
