package cl.duoc.levelupgamer.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import cl.duoc.levelupgamer.viewmodel.RegistrationViewModel
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
    var showDatePicker by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showAddressDialog by remember { mutableStateOf(false) } // Estado para el diálogo de dirección

    val datePickerState = rememberDatePickerState(
        initialDisplayMode = DisplayMode.Input
    )

    // Lógica para el diálogo de Términos y Condiciones
    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = { Text("Términos y Condiciones") },
            text = { Text("Al aceptar confirmas que eres mayor de edad y aceptas el uso de tus datos para brindar el servicio.") },
            confirmButton = { TextButton(onClick = { showTermsDialog = false }) { Text("Cerrar") } }
        )
    }

    // Lógica para el DatePickerDialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("es-CL"))
                            sdf.timeZone = TimeZone.getTimeZone("UTC")
                            vm.onChangeFechaNacimiento(sdf.format(Date(it)))
                        }
                        showDatePicker = false
                    }
                ) { Text("Aceptar") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Lógica para el diálogo de Dirección
    if (showAddressDialog) {
        AlertDialog(
            onDismissRequest = { showAddressDialog = false },
            title = { Text("Dirección de Envío") },
            text = {
                Column {
                    OutlinedTextField(
                        value = form.region,
                        onValueChange = vm::onChangeRegion,
                        label = { Text("Región") },
                        isError = form.regionError != null,
                        supportingText = { form.regionError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = form.comuna,
                        onValueChange = vm::onChangeComuna,
                        label = { Text("Comuna") },
                        isError = form.comunaError != null,
                        supportingText = { form.comunaError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = form.direccion,
                        onValueChange = vm::onChangeDireccion,
                        label = { Text("Dirección (calle y número)") },
                        isError = form.direccionError != null,
                        supportingText = { form.direccionError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAddressDialog = false }) {
                    Text("Aceptar")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Registro", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(16.dp))

            // Campos de texto...
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
                value = form.apellidos,
                onValueChange = vm::onChangeApellidos,
                label = { Text("Apellidos") },
                isError = form.apellidosError != null,
                supportingText = { form.apellidosError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = form.run,
                onValueChange = vm::onChangeRun,
                label = { Text("RUN") },
                placeholder = { Text("Ej: 12.345.678-9") },
                singleLine = true,
                isError = form.runError != null,
                supportingText = { form.runError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = form.email, onValueChange = vm::onChangeEmail, label = { Text("Email") }, isError = form.emailError != null, supportingText = { form.emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = form.contrasena, onValueChange = vm::onChangeContrasena, label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation(), isError = form.contrasenaError != null, supportingText = { form.contrasenaError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = form.contrasenaConfirm, onValueChange = vm::onChangeContrasenaConfirm, label = { Text("Confirmar contraseña") }, visualTransformation = PasswordVisualTransformation(), isError = form.contrasenaConfirmError != null, supportingText = { form.contrasenaConfirmError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))

            // Campo de Fecha de Nacimiento
            Box(modifier = Modifier.clickable { showDatePicker = true }) {
                OutlinedTextField(
                    value = form.fechaNacimiento,
                    onValueChange = {},
                    label = { Text("Fecha de Nacimiento") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(disabledTextColor = MaterialTheme.colorScheme.onSurface, disabledBorderColor = MaterialTheme.colorScheme.outline, disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant),
                    supportingText = { form.fechaNacimientoError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Nuevo campo para abrir el diálogo de dirección
            val direccionMostrada = listOf(form.direccion, form.comuna, form.region).filter { it.isNotBlank() }.joinToString(", ")
            Box(modifier = Modifier.clickable { showAddressDialog = true }) {
                OutlinedTextField(
                    value = direccionMostrada,
                    onValueChange = {},
                    label = { Text("Dirección de Envío") },
                    placeholder = { Text("Presiona para ingresar tu dirección") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(disabledTextColor = MaterialTheme.colorScheme.onSurface, disabledBorderColor = MaterialTheme.colorScheme.outline, disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant),
                    supportingText = { form.direccionError?.let { Text(it, color = MaterialTheme.colorScheme.error) } ?: form.comunaError?.let { Text(it, color = MaterialTheme.colorScheme.error) } ?: form.regionError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = form.codigoReferido,
                onValueChange = vm::onChangeCodigoReferido,
                label = { Text("Código referido (opcional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Checkbox de Términos y Condiciones
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Checkbox(checked = form.aceptaTerminos, onCheckedChange = vm::onChangeAceptaTerminos)
                Text(text = "Acepto los ")
                Text(
                    text = "Términos y Condiciones",
                    modifier = Modifier.clickable { showTermsDialog = true },
                    color = MaterialTheme.colorScheme.secondary,
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Bold
                )
            }
            form.aceptaTerminosError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de Registrarse
            Button(onClick = vm::registrar, enabled = !form.isLoading, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary), modifier = Modifier.fillMaxWidth()) {
                Text("Registrarse", color = MaterialTheme.colorScheme.onSecondary)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Enlace para ir a Iniciar Sesión
            Row {
                Text("¿Ya tienes una cuenta? ")
                Text(
                    text = "Inicia sesión",
                    modifier = Modifier.clickable { onGoToLogin() },
                    color = MaterialTheme.colorScheme.secondary,
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Bold
                )
            }

            // Efectos para mostrar Snackbar y navegar
            LaunchedEffect(form.isSuccess) {
                if (form.isSuccess) {
                    onRegistered()
                    vm.limpiarFormulario()
                }
            }
            LaunchedEffect(form.error) {
                form.error?.let { snackbarHostState.showSnackbar(message = it) }
            }
        }
    }
}