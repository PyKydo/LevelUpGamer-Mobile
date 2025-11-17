package cl.duoc.levelupgamer.viewmodel

import cl.duoc.levelupgamer.model.Usuario
import cl.duoc.levelupgamer.model.repository.InAuthRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@ExperimentalCoroutinesApi
@Suppress("unused")
class RegistrationViewModelTest : StringSpec({

    val testDispatcher = StandardTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(testDispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    "El registro debe ser exitoso con datos válidos" {
        runTest(testDispatcher) {

            val authRepository: InAuthRepository = mockk()
            val dummyUser = Usuario(
                id = 1,
                run = "11.111.111-1",
                nombre = "Nuevo Usuario",
                apellido = "Test",
                email = "nuevo@test.com",
                fechaNacimiento = "2000-01-01"
            )
            coEvery { authRepository.registrar(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns dummyUser
            val viewModel = RegistrationViewModel(authRepository)

            viewModel.onChangeNombre("Nuevo Usuario")
            viewModel.onChangeApellidos("Test")
            viewModel.onChangeRun("11.111.111-1")
            viewModel.onChangeEmail("nuevo@test.com")
            viewModel.onChangeContrasena("ValidPass123!")
            viewModel.onChangeContrasenaConfirm("ValidPass123!")
            viewModel.onChangeFechaNacimiento("01/01/2000")
            viewModel.onChangeRegion("Metropolitana")
            viewModel.onChangeComuna("Santiago")
            viewModel.onChangeDireccion("Av. Siempre Viva 123")
            viewModel.onChangeCodigoReferido("ABCD1234")
            viewModel.onChangeAceptaTerminos(true)
            viewModel.registrar()
            advanceUntilIdle()

            val uiState = viewModel.form.value
            uiState.isSuccess shouldBe true
            uiState.error shouldBe null
        }
    }

    "El registro debe fallar si el email ya existe" {
        runTest(testDispatcher) {
            val authRepository: InAuthRepository = mockk()
            val errorMessage = "El email ya está registrado"
            coEvery { authRepository.registrar(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) } throws IllegalArgumentException(errorMessage)
            val viewModel = RegistrationViewModel(authRepository)

            viewModel.onChangeNombre("Otro Usuario")
            viewModel.onChangeApellidos("Prueba")
            viewModel.onChangeRun("22.222.222-2")
            viewModel.onChangeEmail("existente@test.com")
            viewModel.onChangeContrasena("ValidPass123!")
            viewModel.onChangeContrasenaConfirm("ValidPass123!")
            viewModel.onChangeFechaNacimiento("01/01/2000")
            viewModel.onChangeRegion("Metropolitana")
            viewModel.onChangeComuna("Santiago")
            viewModel.onChangeDireccion("Av. Siempre Viva 123")
            viewModel.onChangeAceptaTerminos(true)
            viewModel.registrar()
            advanceUntilIdle()

            val uiState = viewModel.form.value
            uiState.isSuccess shouldBe false
            uiState.error shouldBe errorMessage
        }
    }

    "El registro debe fallar si la contraseña es inválida" {
        runTest(testDispatcher) {
            val authRepository: InAuthRepository = mockk(relaxed = true)
            val viewModel = RegistrationViewModel(authRepository)

            viewModel.onChangeNombre("Test")
            viewModel.onChangeApellidos("Apellido")
            viewModel.onChangeRun("11.111.111-1")
            viewModel.onChangeEmail("test@test.com")
            viewModel.onChangeContrasena("corta") // Contraseña inválida
            viewModel.onChangeContrasenaConfirm("corta")
            viewModel.onChangeFechaNacimiento("01/01/2000")
            viewModel.onChangeRegion("Metropolitana")
            viewModel.onChangeComuna("Santiago")
            viewModel.onChangeDireccion("Av. Siempre Viva 123")
            viewModel.onChangeAceptaTerminos(true)
            viewModel.registrar()
            advanceUntilIdle()

            val uiState = viewModel.form.value
            uiState.isSuccess shouldBe false
            uiState.contrasenaError shouldNotBe null
            coVerify(exactly = 0) { authRepository.registrar(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) }
        }
    }

    "El registro debe fallar si el usuario es menor de edad" {
        runTest(testDispatcher) {
            val authRepository: InAuthRepository = mockk(relaxed = true)
            val viewModel = RegistrationViewModel(authRepository)
            val fechaMenorDeEdad = java.time.LocalDate.now().minusYears(17).format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))

            viewModel.onChangeNombre("Joven Usuario")
            viewModel.onChangeApellidos("Menor")
            viewModel.onChangeRun("33.333.333-3")
            viewModel.onChangeEmail("joven@test.com")
            viewModel.onChangeContrasena("ValidPass123!")
            viewModel.onChangeContrasenaConfirm("ValidPass123!")
            viewModel.onChangeFechaNacimiento(fechaMenorDeEdad)
            viewModel.onChangeRegion("Metropolitana")
            viewModel.onChangeComuna("Santiago")
            viewModel.onChangeDireccion("Av. Siempre Viva 123")
            viewModel.onChangeAceptaTerminos(true)
            viewModel.registrar()
            advanceUntilIdle()

            val uiState = viewModel.form.value
            uiState.isSuccess shouldBe false
            uiState.fechaNacimientoError shouldBe "Debes tener al menos 18 años."
            coVerify(exactly = 0) { authRepository.registrar(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) }
        }
    }

    "El registro debe fallar si el RUN es inválido" {
        runTest(testDispatcher) {
            val authRepository: InAuthRepository = mockk(relaxed = true)
            val viewModel = RegistrationViewModel(authRepository)

            viewModel.onChangeNombre("Nombre Valido")
            viewModel.onChangeApellidos("Apellido Valido")
            viewModel.onChangeEmail("email.valido@test.com")
            viewModel.onChangeContrasena("ValidPass123!")
            viewModel.onChangeContrasenaConfirm("ValidPass123!")
            viewModel.onChangeFechaNacimiento("01/01/2000")
            viewModel.onChangeRegion("Metropolitana")
            viewModel.onChangeComuna("Santiago")
            viewModel.onChangeDireccion("Av. Siempre Viva 123")
            viewModel.onChangeAceptaTerminos(true)
            viewModel.onChangeRun("11.111.111-2") 
            viewModel.registrar()
            advanceUntilIdle()

            val uiState = viewModel.form.value
            uiState.isSuccess shouldBe false
            uiState.runError shouldBe "RUN inválido"
            coVerify(exactly = 0) { authRepository.registrar(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) }
        }
    }
})