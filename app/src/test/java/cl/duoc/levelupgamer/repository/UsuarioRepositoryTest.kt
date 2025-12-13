package cl.duoc.levelupgamer.repository

import cl.duoc.levelupgamer.data.remote.api.LevelUpApi
import cl.duoc.levelupgamer.data.remote.dto.auth.LoginResponse
import cl.duoc.levelupgamer.data.remote.dto.users.UsuarioRespuestaDto
import cl.duoc.levelupgamer.data.remote.dto.users.UsuarioUpdateDto
import cl.duoc.levelupgamer.data.session.TokenSession
import cl.duoc.levelupgamer.data.session.TokenStore
import cl.duoc.levelupgamer.model.repository.UsuarioRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

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


            val updatedUserDto = UsuarioRespuestaDto(
                id = 1,
                run = "11.111.111-1",
                nombre = "Nuevo",
                apellidos = "Usuario",
                correo = "nuevo@test.com",
                rol = "USER"
            )
            coEvery { mockSecureApi.updateUser(any(), any()) } returns updatedUserDto


            repository.actualizarPerfil("Nuevo", "nuevo@test.com")


            coVerify(exactly = 1) { mockSecureApi.updateUser(originalUserDto.id, any()) }
            coVerify(atLeast = 1) { mockTokenStore.persistSession(any()) }
        }
    }

    "al cambiar la contraseña, debe llamar a la secureApi" {
        runTest {

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


            coEvery { mockSecureApi.changePassword(any()) } returns Unit


            repository.changePassword("pass-vieja", "pass-nueva")


            coVerify(exactly = 1) { mockSecureApi.changePassword(any()) }
        }
    }

    "al actualizar el perfil elimina espacios y conserva los tokens" {
        runTest {
            val originalEmail = "antiguo@test.com"
            val originalUserDto = UsuarioRespuestaDto(
                id = 7,
                run = "22.222.222-2",
                nombre = "Antiguo",
                apellidos = "Usuario",
                correo = originalEmail,
                rol = "USER"
            )
            val loginResponse = LoginResponse(accessToken = "token", refreshToken = "refresh", usuario = originalUserDto)
            coEvery { mockAuthApi.login(any()) } returns loginResponse
            repository.iniciarSesion(originalEmail, "password")

            val dtoSlot = slot<UsuarioUpdateDto>()
            val updatedUserDto = UsuarioRespuestaDto(
                id = 7,
                run = "22.222.222-2",
                nombre = "Nuevo Nombre",
                apellidos = "Usuario",
                correo = "nuevo@test.com",
                rol = "USER"
            )
            coEvery { mockSecureApi.updateUser(any(), capture(dtoSlot)) } returns updatedUserDto
            coEvery { mockTokenStore.currentSession() } returns TokenSession(accessToken = "token", refreshToken = "refresh", userId = originalUserDto.id, email = originalEmail)

            repository.actualizarPerfil("   Nuevo Nombre   ", "   nuevo@test.com   ")

            dtoSlot.captured.nombre shouldBe "Nuevo Nombre"
            dtoSlot.captured.correo shouldBe "nuevo@test.com"
        }
    }

    "si la api rechaza la actualizacion del perfil se conserva el usuario actual" {
        runTest {
            val originalEmail = "activo@test.com"
            val userDto = UsuarioRespuestaDto(
                id = 9,
                run = "33.333.333-3",
                nombre = "Activo",
                apellidos = "Usuario",
                correo = originalEmail,
                rol = "USER"
            )
            val loginResponse = LoginResponse(accessToken = "token", refreshToken = "refresh", usuario = userDto)
            coEvery { mockAuthApi.login(any()) } returns loginResponse
            repository.iniciarSesion(originalEmail, "password")

            val errorBody = """{"correo":"Dominio de correo no permitido"}"""
                .toResponseBody("application/json".toMediaType())
            val errorResponse = Response.error<UsuarioRespuestaDto>(400, errorBody)
            coEvery { mockSecureApi.updateUser(any(), any()) } throws HttpException(errorResponse)
            coEvery { mockTokenStore.currentSession() } returns TokenSession(accessToken = "token", refreshToken = "refresh", userId = userDto.id, email = originalEmail)

            shouldThrow<HttpException> {
                repository.actualizarPerfil("Nuevo", "nuevo@test.com")
            }

            repository.usuarioActual.value?.email shouldBe originalEmail
            coVerify(exactly = 1) { mockTokenStore.persistSession(any()) }
        }
    }
})