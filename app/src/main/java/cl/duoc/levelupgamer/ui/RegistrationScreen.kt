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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(registrationViewModel: RegistrationViewModel = viewModel()) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showDatePicker by remember { mutableStateOf(false) }
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
            if (showDatePicker) {
                val spanishLocale = Locale("es")
                val newConfig = Configuration(LocalConfiguration.current)
                newConfig.setLocale(spanishLocale)
                
                CompositionLocalProvider(LocalConfiguration provides newConfig) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    val selectedMillis = datePickerState.selectedDateMillis
                                    if (selectedMillis != null) {
                                        val sdf = SimpleDateFormat("dd/MM/yyyy", spanishLocale)
                                        sdf.timeZone = TimeZone.getTimeZone("UTC")
                                        birthDate = sdf.format(Date(selectedMillis))
                                    }
                                    showDatePicker = false
                                }
                            ) { Text("Aceptar") }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { showDatePicker = false }
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
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email (@duoc.cl, @profesor.duoc.cl, etc)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = birthDate,
                onValueChange = { },
                label = { Text("Fecha de Nacimiento") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
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
                onClick = {
                    registrationViewModel.onRegistrationClick(
                        name = name,
                        email = email,
                        birthDate = birthDate,
                        onSuccess = {
                            scope.launch {
                                snackbarHostState.showSnackbar(it)
                            }
                        },
                        onError = {
                            scope.launch {
                                snackbarHostState.showSnackbar(it)
                            }
                        }
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrarse", color = MaterialTheme.colorScheme.onSecondary)
            }
        }
    }
}