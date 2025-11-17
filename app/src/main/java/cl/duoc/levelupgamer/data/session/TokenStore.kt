package cl.duoc.levelupgamer.data.session

import kotlinx.coroutines.flow.StateFlow

interface TokenStore {
    val sessionFlow: StateFlow<TokenSession>

    fun currentSession(): TokenSession

    suspend fun persistSession(session: TokenSession)

    suspend fun clear()
}
