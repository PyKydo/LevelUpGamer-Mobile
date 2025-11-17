package cl.duoc.levelupgamer.viewmodel

import cl.duoc.levelupgamer.model.Usuario
import cl.duoc.levelupgamer.model.local.dao.UsuarioDao
import cl.duoc.levelupgamer.model.repository.UsuarioRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@ExperimentalCoroutinesApi
class LoginViewModelTest : StringSpec({

    val testDispatcher = StandardTestDispatcher()

    lateinit var viewModel: LoginViewModel
    val authRepository: cl.duoc.levelupgamer.model.repository.InAuthRepository = mockk()

    val dummyUser = Usuario(1, "David", "david@test.com", "123456", "01/01/1990")

    beforeTest {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(authRepository)
    }

    afterTest {
        Dispatchers.resetMain()
    }

    "iniciarSesion con credenciales correctas debe resultar en éxito" {
        // Preparación
        coEvery { authRepository.iniciarSesion("david@test.com", "123456") } returns dummyUser

        // Acción
        viewModel.onChangeEmail("david@test.com")
        viewModel.onChangeContrasena("123456")
        viewModel.iniciarSesion()
        testDispatcher.scheduler.advanceUntilIdle()

        // Verificación
        val uiState = viewModel.form.value
        uiState.isSuccess shouldBe true
        uiState.error shouldBe null
    }

    "iniciarSesion con contraseña incorrecta debe resultar en error" {
        // Preparación
        coEvery { authRepository.iniciarSesion("david@test.com", "contraseña-incorrecta") } throws IllegalArgumentException("Contraseña incorrecta")

        // Acción
        viewModel.onChangeEmail("david@test.com")
        viewModel.onChangeContrasena("contraseña-incorrecta")
        viewModel.iniciarSesion()
        testDispatcher.scheduler.advanceUntilIdle()

        // Verificación
        val uiState = viewModel.form.value
        uiState.isSuccess shouldBe false
        uiState.error shouldNotBe null
        uiState.error shouldBe "Contraseña incorrecta"
    }

    "iniciarSesion con email incorrecto debe resultar en error" {
        // Preparación
        coEvery { authRepository.iniciarSesion("not-exist@test.com", "123456") } throws IllegalStateException("Usuario no encontrado")

        // Acción
        viewModel.onChangeEmail("not-exist@test.com")
        viewModel.onChangeContrasena("123456")
        viewModel.iniciarSesion()
        testDispatcher.scheduler.advanceUntilIdle()

        // Verificación
        val uiState = viewModel.form.value
        uiState.isSuccess shouldBe false
        uiState.error shouldNotBe null
        uiState.error shouldBe "Usuario no encontrado"
    }
})