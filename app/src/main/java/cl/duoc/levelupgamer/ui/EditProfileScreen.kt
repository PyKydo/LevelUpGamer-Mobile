package cl.duoc.levelupgamer.ui

import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.levelupgamer.model.Usuario
import cl.duoc.levelupgamer.ui.components.FormTextField
import cl.duoc.levelupgamer.ui.components.GlowCard
import cl.duoc.levelupgamer.ui.components.PrimaryActionButton
import cl.duoc.levelupgamer.ui.components.SecondaryActionButton
import cl.duoc.levelupgamer.ui.theme.LocalLevelUpSpacing

private val AllowedEmailDomains = listOf(
    "gmail.com",
    "hotmail.com",
    "outlook.com",
    "yahoo.com",
    "duoc.cl",
    "profesor.duoc.cl",
    "duocuc.cl"
)
private const val MaxEmailLength = 100

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    user: Usuario, 
    onBackClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onSaveChanges: (newName: String, newEmail: String) -> Unit,
    errorMessage: String? = null
) {
    var name by remember { mutableStateOf(user.nombre) }
    var email by remember { mutableStateOf(user.email) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    val spacing = LocalLevelUpSpacing.current

    fun validateAndSave() {
        val trimmedName = name.trim()
        val trimmedEmail = email.trim()
        var hasError = false
        if (trimmedName.isBlank()) {
            nameError = "Ingresa tu nombre"
            hasError = true
        } else {
            nameError = null
        }
        when {
            trimmedEmail.isBlank() -> {
                emailError = "Ingresa un correo"
                hasError = true
            }
            !Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches() -> {
                emailError = "Ingresa un correo válido"
                hasError = true
            }
            trimmedEmail.length > MaxEmailLength -> {
                emailError = "El correo no puede superar 100 caracteres"
                hasError = true
            }
            !hasAllowedDomain(trimmedEmail) -> {
                emailError = "Solo se permiten correos Gmail, Outlook, Yahoo o Duoc"
                hasError = true
            }
            else -> {
                emailError = null
            }
        }
        if (!hasError) {
            onSaveChanges(trimmedName, trimmedEmail)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Perfil") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = spacing.mdDp, vertical = spacing.smDp),
            verticalArrangement = Arrangement.spacedBy(spacing.mdDp)
        ) {
            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            GlowCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.smDp)) {
                    Text(
                        text = "Datos personales",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    FormTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            if (nameError != null) nameError = null
                        },
                        label = "Nombre",
                        modifier = Modifier.fillMaxWidth(),
                        supportingText = nameError,
                        isError = nameError != null
                    )
                    FormTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            if (emailError != null) emailError = null
                        },
                        label = "Email",
                        modifier = Modifier.fillMaxWidth(),
                        supportingText = emailError,
                        isError = emailError != null
                    )
                }
            }
            SecondaryActionButton(
                text = "Cambiar contraseña",
                modifier = Modifier.fillMaxWidth(),
                onClick = onChangePasswordClick
            )
            Spacer(modifier = Modifier.weight(1f))
            PrimaryActionButton(
                text = "Guardar cambios",
                modifier = Modifier.fillMaxWidth(),
                onClick = { validateAndSave() }
            )
        }
    }
}

private fun hasAllowedDomain(email: String): Boolean {
    val domain = email.substringAfterLast("@", "")
    return domain.isNotEmpty() && AllowedEmailDomains.any { it.equals(domain, ignoreCase = true) }
}