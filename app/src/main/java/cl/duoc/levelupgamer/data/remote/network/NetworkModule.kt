package cl.duoc.levelupgamer.data.remote.network

import cl.duoc.levelupgamer.data.remote.api.LevelUpApi
import cl.duoc.levelupgamer.data.remote.api.LevelUpAuthCallApi
import cl.duoc.levelupgamer.data.session.TokenStore
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class NetworkModule(
    private val tokenStore: TokenStore,
    private val apiBaseUrl: String,
    private val isDebug: Boolean
) {

    private val gson = GsonBuilder().setLenient().create()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (isDebug) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.BASIC
        }
    }

    private val baseClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .callTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .build()

    private val authRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(apiBaseUrl)
        .client(baseClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    private val refreshApi: LevelUpAuthCallApi = authRetrofit.create(LevelUpAuthCallApi::class.java)

    private val secureClient: OkHttpClient = baseClient.newBuilder()
        .addInterceptor(AuthInterceptor(tokenStore))
        .authenticator(TokenAuthenticator(tokenStore, refreshApi))
        .build()

    private val secureRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(apiBaseUrl)
        .client(secureClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val authApi: LevelUpApi = authRetrofit.create(LevelUpApi::class.java)
    val securedApi: LevelUpApi = secureRetrofit.create(LevelUpApi::class.java)

    companion object {
        private const val DEFAULT_TIMEOUT_SECONDS = 10L
    }
}
