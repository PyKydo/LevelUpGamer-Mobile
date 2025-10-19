package cl.duoc.levelupgamer.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cl.duoc.levelupgamer.R
import cl.duoc.levelupgamer.viewmodel.LoginViewModel
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    vm: LoginViewModel,
    onRegisterClick: () -> Unit,
    onLoggedIn: () -> Unit
) {
    val form by vm.form.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

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
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo de LevelUp Gamer",
                modifier = Modifier.size(150.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Iniciar Sesión", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = form.email,
                onValueChange = vm::onChangeEmail,
                label = { Text("Email") },
                isError = form.emailError != null,
                supportingText = {
                    form.emailError?.let { err ->
                        Text(text = err, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = form.contrasena,
                onValueChange = vm::onChangeContrasena,
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                isError = form.contrasenaError != null,
                supportingText = {
                    form.contrasenaError?.let { err ->
                        Text(text = err, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = vm::iniciarSesion,
                enabled = !form.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (form.isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onSecondary,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Text("Iniciar Sesión", color = MaterialTheme.colorScheme.onSecondary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "¿No tienes una cuenta? Regístrate",
                modifier = Modifier.clickable { onRegisterClick() },
                color = MaterialTheme.colorScheme.secondary
            )

            LaunchedEffect(form.isSuccess) {
                if (form.isSuccess) {
                    onLoggedIn()
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