package cl.duoc.levelupgamer.data.session

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class SecureTokenStore(context: Context) : TokenStore {

    private val appContext = context.applicationContext
    private val masterKey = MasterKey.Builder(appContext)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        appContext,
        PREF_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val _session = MutableStateFlow(readSession())
    override val sessionFlow: StateFlow<TokenSession> = _session.asStateFlow()

    override fun currentSession(): TokenSession = _session.value

    override suspend fun persistSession(session: TokenSession) {
        withContext(Dispatchers.IO) {
            prefs.edit {
                putString(KEY_ACCESS, session.accessToken)
                putString(KEY_REFRESH, session.refreshToken)
                putString(KEY_USER_ID, session.userId?.toString())
                putString(KEY_EMAIL, session.email)
                putString(KEY_ROLE, session.role)
            }
        }
        _session.value = session
    }

    override suspend fun clear() {
        withContext(Dispatchers.IO) { prefs.edit { clear() } }
        _session.value = TokenSession()
    }

    private fun readSession(): TokenSession = TokenSession(
        accessToken = prefs.getString(KEY_ACCESS, null),
        refreshToken = prefs.getString(KEY_REFRESH, null),
        userId = prefs.getString(KEY_USER_ID, null)?.toLongOrNull(),
        email = prefs.getString(KEY_EMAIL, null),
        role = prefs.getString(KEY_ROLE, null)
    )

    private companion object {
        const val PREF_NAME = "level_up_session"
        const val KEY_ACCESS = "access"
        const val KEY_REFRESH = "refresh"
        const val KEY_USER_ID = "user_id"
        const val KEY_EMAIL = "email"
        const val KEY_ROLE = "role"
    }
}
