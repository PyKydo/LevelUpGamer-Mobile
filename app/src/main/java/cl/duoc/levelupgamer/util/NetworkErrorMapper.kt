package cl.duoc.levelupgamer.util

import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException
import org.json.JSONArray
import org.json.JSONObject

object NetworkErrorMapper {
    fun map(t: Throwable): String = when (t) {
        is HttpException -> mapHttpException(t)
        is SocketTimeoutException, is ConnectException, is UnknownHostException ->
            "No se pudo conectar al servidor. Revisa tu conexión e intenta nuevamente."
        is SSLException -> "Error de seguridad durante la conexión. Revisa tu configuración de red."
        is IOException -> "Error de comunicación. Revisa tu conexión e intenta otra vez."
        else -> t.message ?: "Ocurrió un error inesperado. Intenta nuevamente más tarde."
    }

    private fun mapHttpException(e: HttpException): String = when (e.code()) {
        400 -> extractMessageOrDefault(e, "Solicitud inválida.")
        401 -> "Credenciales incorrectas. Revisa tu email y contraseña."
        403 -> "No tienes permisos para realizar esta acción."
        404 -> "Recurso no encontrado en el servidor."
        500, 502, 503, 504 -> "Error del servidor. Intenta nuevamente más tarde."
        else -> extractMessageOrDefault(e, "Ocurrió un error de red (código ${e.code()}).")
    }

    private fun extractMessageOrDefault(e: HttpException, default: String): String {
        return try {
            val body = e.response()?.errorBody()?.string()
            if (!body.isNullOrBlank()) {
                val trimmed = body.trim()
                if (trimmed.startsWith("{")) {
                    try {
                        val obj = JSONObject(trimmed)
                        val candidates = listOf("error", "message", "detail", "errors")
                        for (key in candidates) {
                            if (obj.has(key)) {
                                val value = obj.get(key)
                                when (value) {
                                    is String -> if (value.isNotBlank()) return value
                                    is JSONArray -> if (value.length() > 0) return value.optString(0)
                                    is JSONObject -> {
                                        
                                        val nested = value.optString("message")
                                        if (!nested.isNullOrBlank()) return nested
                                    }
                                }
                            }
                        }
                    } catch (_: Exception) {
                        
                    }
                }
                
                if (trimmed.length <= 250) trimmed else default
            } else default
        } catch (_: Exception) {
            default
        }
    }
}
