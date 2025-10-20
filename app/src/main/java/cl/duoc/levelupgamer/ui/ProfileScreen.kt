package cl.duoc.levelupgamer.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.levelupgamer.R
import cl.duoc.levelupgamer.model.Usuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(user: Usuario, onEditClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi perfil") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.secondary,
                    titleContentColor = colorScheme.onSecondary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // <-- Se usa el padding del Scaffold
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Sección superior: Avatar y nombre principal
            Image(
                painter = painterResource(id = R.drawable.perfil_usuario),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = user.nombre,
                style = typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = user.email,
                style = typography.bodyMedium,
                color = colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Recuadro con la información detallada
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(0.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Campo Nombre Completo
                    Column {
                        Text("Nombre Completo", style = typography.labelSmall, color = colorScheme.onSurfaceVariant)
                        Text(user.nombre, style = typography.bodyLarge)
                    }
                    HorizontalDivider()
                    // Campo Gmail
                    Column {
                        Text("Gmail", style = typography.labelSmall, color = colorScheme.onSurfaceVariant)
                        Text(user.email, style = typography.bodyLarge)
                    }
                    HorizontalDivider()
                    // Campo Año de Nacimiento
                    Column {
                        Text("Fecha de Nacimiento", style = typography.labelSmall, color = colorScheme.onSurfaceVariant)
                        Text(user.fechaNacimiento, style = typography.bodyLarge)
                    }
                }
            }

            // Este Spacer empuja los botones hacia abajo
            Spacer(modifier = Modifier.weight(1f))

            // --- BOTONES MODIFICADOS ---
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = onEditClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Green,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Editar Perfil")
                }

                Button(
                    onClick = { /* Lógica de cierre de sesión deshabilitada */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Green,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Cerrar Sesión")
                }
            }
        }
    }
}
