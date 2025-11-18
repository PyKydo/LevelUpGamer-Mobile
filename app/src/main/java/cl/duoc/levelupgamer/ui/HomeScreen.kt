package cl.duoc.levelupgamer.ui

import coil.compose.AsyncImage
import androidx.compose.foundation.Image
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.levelupgamer.model.Blog
import cl.duoc.levelupgamer.model.Producto

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    productosDestacados: List<Producto>,
    blogsDestacados: List<Blog>,
    onProductClick: (Producto) -> Unit,
    onBlogClick: (Blog) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inicio") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(16.dp).padding(innerPadding), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Productos destacados", style = MaterialTheme.typography.titleMedium)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(productosDestacados) { p ->
                    Card(modifier = Modifier
                        .size(120.dp)
                        .clickable { onProductClick(p) }) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(p.nombre, style = MaterialTheme.typography.bodyMedium)
                            Text("$${String.format("%.2f", p.precio)}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            Text("Blogs destacados", style = MaterialTheme.typography.titleMedium)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(blogsDestacados) { b ->
                    Card(modifier = Modifier
                        .size(220.dp)
                        .clickable { onBlogClick(b) }) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            val model = b.imageUrl
                            if (!model.isNullOrBlank()) {
                                AsyncImage(model = model, contentDescription = b.title, modifier = Modifier.size(120.dp))
                            } else {
                                Image(painter = androidx.compose.ui.res.painterResource(id = cl.duoc.levelupgamer.R.drawable.logo), contentDescription = "placeholder", modifier = Modifier.size(120.dp))
                            }
                            Text(b.title, style = MaterialTheme.typography.bodyMedium)
                            Text(b.summary, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            Text("Más información", style = MaterialTheme.typography.titleMedium)
            Text("Bienvenido a la tienda. Explora productos y artículos para mejorar tus partidas.")
        }
    }
}
