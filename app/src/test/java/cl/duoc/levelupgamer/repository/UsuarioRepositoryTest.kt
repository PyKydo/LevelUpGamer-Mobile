package cl.duoc.levelupgamer.repository

import cl.duoc.levelupgamer.data.remote.api.LevelUpApi
import cl.duoc.levelupgamer.data.remote.dto.auth.LoginResponse
import cl.duoc.levelupgamer.data.remote.dto.users.UsuarioRespuestaDto
import cl.duoc.levelupgamer.data.session.TokenSession
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
@Suppress("unused")
class UsuarioRepositoryTest : StringSpec({

    val mockAuthApi: LevelUpApi = mockk()
    val mockSecureApi: LevelUpApi = mockk()
    val mockTokenStore: TokenStore = mockk(relaxed = true)
    lateinit var repository: UsuarioRepository

    beforeTest {
        coEvery { mockTokenStore.currentSession() } returns TokenSession()
        repository = UsuarioRepository(mockAuthApi, mockSecureApi, mockTokenStore, Dispatchers.Unconfined)
    }

    "al iniciar sesion con exito, debe llamar a la api y persistir la sesion" {
        runTest {
            val email = "test@test.com"
            val password = "password123"
            val mockUserDto = UsuarioRespuestaDto(
                id = 1,
                run = "11.111.111-1",
                nombre = "Test",
                apellidos = "User",
                correo = email,
                rol = "USER"
            )
            val mockResponse = LoginResponse(accessToken = "fake-access-token", refreshToken = "fake-refresh-token", usuario = mockUserDto)
            coEvery { mockAuthApi.login(any()) } returns mockResponse

            repository.iniciarSesion(email, password)

            coVerify(exactly = 1) { mockAuthApi.login(any()) }
            coVerify(exactly = 1) { mockTokenStore.persistSession(any()) }
        }
    }

    "al cerrar sesion, debe limpiar el tokenStore" {
        runTest {
            coEvery { mockTokenStore.clear() } just runs
            repository.cerrarSesion()
            coVerify(exactly = 1) { mockTokenStore.clear() }
        }
    }

    "al registrar un usuario, debe llamar a la api" {
        runTest {
            val mockUserDto = UsuarioRespuestaDto(
                id = 1,
                run = "11.111.111-1",
                nombre = "Nuevo",
                apellidos = "Usuario",
                correo = "nuevo@test.com",
                rol = "USER"
            )
            coEvery { mockAuthApi.register(any()) } returns mockUserDto

            repository.registrar(
                run = "11.111.111-1",
                nombre = "Nuevo",
                apellidos = "Usuario",
                correo = "nuevo@test.com",
                contrasena = "pass123",
                fechaNacimiento = "2000-01-01",
                region = "RM",
                comuna = "Santiago",
                direccion = "Av. Siempre Viva 123",
                codigoReferido = null
            )

            coVerify(exactly = 1) { mockAuthApi.register(any()) }
        }
    }

    "al actualizar el perfil, debe llamar a la secureApi y persistir la sesion" {
        runTest {
            // 1. Preparación
            val originalEmail = "antiguo@test.com"
            val originalUserDto = UsuarioRespuestaDto(
                id = 1,
                run = "11.111.111-1",
                nombre = "Antiguo",
                apellidos = "Usuario",
                correo = originalEmail,
                rol = "USER"
            )
            val loginResponse = LoginResponse(accessToken = "fake-token", refreshToken = "fake-token", usuario = originalUserDto)
            coEvery { mockAuthApi.login(any()) } returns loginResponse
            repository.iniciarSesion(originalEmail, "password")

            // 2. Preparación
            val updatedUserDto = UsuarioRespuestaDto(
                id = 1,
                run = "11.111.111-1",
                nombre = "Nuevo",
                apellidos = "Usuario",
                correo = "nuevo@test.com",
                rol = "USER"
            )
            coEvery { mockSecureApi.updateUser(any(), any()) } returns updatedUserDto

            // 3. Acción
            repository.actualizarPerfil("Nuevo", "nuevo@test.com")

            // 4. Verificación
            coVerify(exactly = 1) { mockSecureApi.updateUser(originalUserDto.id, any()) }
            coVerify(atLeast = 1) { mockTokenStore.persistSession(any()) } // Se llama en login y en update
        }
    }

    "al cambiar la contraseña, debe llamar a la secureApi" {
        runTest {
            // 1. Preparación
            val email = "test@test.com"
            val userDto = UsuarioRespuestaDto(
                id = 1,
                run = "11.111.111-1",
                nombre = "Test",
                apellidos = "User",
                correo = email,
                rol = "USER"
            )
            val loginResponse = LoginResponse(accessToken = "fake-token", refreshToken = "fake-token", usuario = userDto)
            coEvery { mockAuthApi.login(any()) } returns loginResponse
            repository.iniciarSesion(email, "password")

            // 2. Preparación
            coEvery { mockSecureApi.changePassword(any()) } returns Unit

            // 3. Acción
            repository.changePassword("pass-vieja", "pass-nueva")

            // 4. Verificación
            coVerify(exactly = 1) { mockSecureApi.changePassword(any()) }
        }
    }
})