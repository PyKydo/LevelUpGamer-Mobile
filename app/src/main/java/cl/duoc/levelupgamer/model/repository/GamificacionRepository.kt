package cl.duoc.levelupgamer.model.repository

import cl.duoc.levelupgamer.data.remote.api.LevelUpApi
import cl.duoc.levelupgamer.data.remote.dto.gamificacion.PuntosDto
import cl.duoc.levelupgamer.data.remote.mapper.toDomain
import cl.duoc.levelupgamer.model.PuntosBalance
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GamificacionRepository(
    private val api: LevelUpApi,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun obtenerPuntos(userId: Long): PuntosBalance = withContext(ioDispatcher) {
        api.getPoints(userId).toDomain()
    }

    suspend fun sumarPuntos(userId: Long, puntos: Int, motivo: String? = null): PuntosBalance =
        withContext(ioDispatcher) {
            api.earnPoints(PuntosDto(userId = userId, puntos = puntos, motivo = motivo)).toDomain()
        }

    suspend fun canjearPuntos(userId: Long, puntos: Int, motivo: String? = null): PuntosBalance =
        withContext(ioDispatcher) {
            api.redeemPoints(PuntosDto(userId = userId, puntos = puntos, motivo = motivo)).toDomain()
        }
}
