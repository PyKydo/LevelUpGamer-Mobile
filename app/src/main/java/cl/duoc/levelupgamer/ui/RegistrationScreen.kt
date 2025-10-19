package cl.duoc.levelupgamer.ui

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.levelupgamer.viewmodel.RegistrationViewModel
import cl.duoc.levelupgamer.viewmodel.RegistrationViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    vm: RegistrationViewModel,
    onRegistered: () -> Unit,
    onGoToLogin: () -> Unit
) {
    val form by vm.form.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showDatePicker = remember { androidx.compose.runtime.mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialDisplayMode = DisplayMode.Input
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showDatePicker.value) {
                val spanishLocale = Locale.forLanguageTag("es-CL")
                val newConfig = Configuration(LocalConfiguration.current)
                newConfig.setLocale(spanishLocale)
                
                CompositionLocalProvider(LocalConfiguration provides newConfig) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker.value = false },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    val selectedMillis = datePickerState.selectedDateMillis
                                    if (selectedMillis != null) {
                                        val sdf = SimpleDateFormat("dd/MM/yyyy", spanishLocale)
                                        sdf.timeZone = TimeZone.getTimeZone("UTC")
                                        val fecha = sdf.format(Date(selectedMillis))
                                        vm.onChangeFechaNacimiento(fecha)
                                    }
                                    showDatePicker.value = false
                                }
                            ) { Text("Aceptar") }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { showDatePicker.value = false }
                            ) { Text("Cancelar") }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }
            }

            Text(text = "Registro", style = MaterialTheme.typography.headlineLarge)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = form.nombre,
                onValueChange = vm::onChangeNombre,
                label = { Text("Nombre") },
                isError = form.nombreError != null,
                supportingText = { form.nombreError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = form.email,
                onValueChange = vm::onChangeEmail,
                label = { Text("Email") },
                isError = form.emailError != null,
                supportingText = { form.emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = form.contrasena,
                onValueChange = vm::onChangeContrasena,
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                isError = form.contrasenaError != null,
                supportingText = { form.contrasenaError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = form.fechaNacimiento,
                onValueChange = { },
                label = { Text("Fecha de Nacimiento") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker.value = true },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = vm::registrar,
                enabled = !form.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrarse", color = MaterialTheme.colorScheme.onSecondary)
            }

            LaunchedEffect(form.isSuccess) {
                if (form.isSuccess) {
                    onRegistered()
                    vm.limpiarFormulario()
                }
            }

            LaunchedEffect(form.error) {
                form.error?.let { msg ->
                    snackbarHostState.showSnackbar(message = msg)
                }
            }
        }
    }
}