package cl.duoc.levelupgamer.di

import android.content.Context
import cl.duoc.levelupgamer.data.remote.network.NetworkModule
import cl.duoc.levelupgamer.data.session.SecureTokenStore
import cl.duoc.levelupgamer.data.session.TokenStore
import cl.duoc.levelupgamer.model.local.AppDatabase
import cl.duoc.levelupgamer.model.repository.CarritoRepository
import cl.duoc.levelupgamer.model.repository.GamificacionRepository
import cl.duoc.levelupgamer.model.repository.PedidoRepository
import cl.duoc.levelupgamer.model.repository.ProductoRepository
import cl.duoc.levelupgamer.model.repository.UsuarioRepository

class ServiceLocator private constructor(context: Context) {

    private val appContext = context.applicationContext

    private val databaseInternal: AppDatabase by lazy { AppDatabase.get(appContext) }
    private val tokenStoreInternal: TokenStore by lazy { SecureTokenStore(appContext) }

    private val apiBaseUrl: String by lazy { readBuildConfigString(appContext, "API_BASE_URL", "http://10.0.2.2:8080/") }
    private val isDebug: Boolean by lazy { readBuildConfigBoolean(appContext, "DEBUG", false) }

    private val networkModule: NetworkModule by lazy { NetworkModule(tokenStoreInternal, apiBaseUrl, isDebug) }

    val database: AppDatabase
        get() = databaseInternal

    val tokenStore: TokenStore
        get() = tokenStoreInternal

    val usuarioRepository: UsuarioRepository by lazy {
        UsuarioRepository(
            authApi = networkModule.authApi,
            secureApi = networkModule.securedApi,
            tokenStore = tokenStoreInternal
        )
    }

    val productoRepository: ProductoRepository by lazy {
        ProductoRepository(
            dao = databaseInternal.productoDao(),
            api = networkModule.securedApi
        )
    }

    val carritoRepository: CarritoRepository by lazy {
        CarritoRepository(
            dao = databaseInternal.carritoItemDao(),
            api = networkModule.securedApi
        )
    }

    val pedidoRepository: PedidoRepository by lazy {
        PedidoRepository(api = networkModule.securedApi)
    }

    val gamificacionRepository: GamificacionRepository by lazy {
        GamificacionRepository(api = networkModule.securedApi)
    }

    companion object {
        @Volatile
        private var instance: ServiceLocator? = null

        fun get(context: Context): ServiceLocator = instance ?: synchronized(this) {
            instance ?: ServiceLocator(context).also { instance = it }
        }

        private fun readBuildConfigString(context: Context, fieldName: String, default: String): String {
            return try {
                val clazz = Class.forName("${context.packageName}.BuildConfig")
                val field = clazz.getDeclaredField(fieldName)
                field.get(null) as? String ?: default
            } catch (t: Throwable) {
                default
            }
        }

        private fun readBuildConfigBoolean(context: Context, fieldName: String, default: Boolean): Boolean {
            return try {
                val clazz = Class.forName("${context.packageName}.BuildConfig")
                val field = clazz.getDeclaredField(fieldName)
                field.getBoolean(null)
            } catch (t: Throwable) {
                default
            }
        }
    }
}
