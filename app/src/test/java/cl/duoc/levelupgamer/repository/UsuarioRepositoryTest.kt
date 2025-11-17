package cl.duoc.levelupgamer.repository

import cl.duoc.levelupgamer.data.remote.api.LevelUpApi
import cl.duoc.levelupgamer.data.remote.dto.auth.LoginResponse
import cl.duoc.levelupgamer.data.remote.dto.users.UsuarioRespuestaDto
import cl.duoc.levelupgamer.data.session.TokenStore
import cl.duoc.levelupgamer.model.repository.UsuarioRepository
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@ExperimentalCoroutinesApi
class UsuarioRepositoryTest : StringSpec({

    "al iniciar sesion con exito, debe llamar a la api y persistir la sesion" {
        runTest {
            // 1. Preparación
            val authApi: LevelUpApi = mockk()
            val secureApi: LevelUpApi = mockk()
            val tokenStore: TokenStore = mockk(relaxed = true)
            coEvery { tokenStore.currentSession() } returns mockk(relaxed = true) // Evitar que el init falle
            val repository = UsuarioRepository(authApi, secureApi, tokenStore, Dispatchers.Unconfined)

            val email = "test@test.com"
            val password = "password123"
            val mockUserDto = UsuarioRespuestaDto(id = 1, email = email, nombre = "Test", rol = "USER")
            val mockResponse = LoginResponse(accessToken = "fake-access-token", refreshToken = "fake-refresh-token", usuario = mockUserDto)

            coEvery { authApi.login(any()) } returns mockResponse

            // 2. Acción
            repository.iniciarSesion(email, password)

            // 3. Verificación
            coVerify(exactly = 1) { authApi.login(any()) }
            coVerify(exactly = 1) { tokenStore.persistSession(any()) }
        }
    }

    "al cerrar sesion, debe limpiar el tokenStore" {
        runTest {
            // 1. Preparación
            val authApi: LevelUpApi = mockk()
            val secureApi: LevelUpApi = mockk()
            val tokenStore: TokenStore = mockk()
            coEvery { tokenStore.clear() } just runs
            coEvery { tokenStore.currentSession() } returns mockk(relaxed = true)
            val repository = UsuarioRepository(authApi, secureApi, tokenStore, Dispatchers.Unconfined)

            // 2. Acción
            repository.cerrarSesion()

            // 3. Verificación
            coVerify(atLeast = 1) { tokenStore.clear() }
        }
    }

    "al registrar un usuario, debe llamar a la api" {
        runTest {
            // 1. Preparación
            val authApi: LevelUpApi = mockk()
            val secureApi: LevelUpApi = mockk()
            val tokenStore: TokenStore = mockk(relaxed = true)
            coEvery { tokenStore.currentSession() } returns mockk(relaxed = true)
            val repository = UsuarioRepository(authApi, secureApi, tokenStore, Dispatchers.Unconfined)

            val mockUserDto = UsuarioRespuestaDto(id = 1, email = "nuevo@test.com", nombre = "Nuevo", rol = "USER")

            coEvery { authApi.register(any()) } returns mockUserDto

            // 2. Acción
            repository.registrar(
                nombre = "Nuevo",
                run = null,
                apellido = null,
                email = "nuevo@test.com",
                contrasena = "pass123",
                fechaNacimiento = "01/01/1990",
                telefono = null,
                region = null,
                comuna = null,
                direccion = null
            )

            // 3. Verificación
            coVerify(exactly = 1) { authApi.register(any()) }
        }
    }
})