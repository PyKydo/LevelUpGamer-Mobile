package cl.duoc.levelupgamer.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.ExperimentalMaterial3Api
import kotlin.Suppress
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import cl.duoc.levelupgamer.BuildConfig
import cl.duoc.levelupgamer.model.Blog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogsScreen(
    blogs: List<Blog>,
    onBlogClick: (Blog) -> Unit
) {
    androidx.compose.material3.Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { Text("Blogs") },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(blogs) { b ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onBlogClick(b) }) {
                    Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.Start) {
                        val imgUrl = b.imageUrl
                        val model = when {
                            imgUrl.isNullOrBlank() -> null
                            imgUrl.startsWith("http", ignoreCase = true) -> imgUrl
                            imgUrl.startsWith("/") -> BuildConfig.API_BASE_URL.trimEnd('/') + imgUrl
                            else -> null
                        }

                        if (model != null) {
                            AsyncImage(
                                model = model,
                                contentDescription = "Blog image",
                                placeholder = painterResource(id = cl.duoc.levelupgamer.R.drawable.logo),
                                error = painterResource(id = cl.duoc.levelupgamer.R.drawable.logo),
                                modifier = Modifier.size(72.dp)
                            )
                        } else {
                            Image(
                                painter = painterResource(id = cl.duoc.levelupgamer.R.drawable.logo),
                                contentDescription = "Blog placeholder",
                                modifier = Modifier.size(72.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(b.title, style = MaterialTheme.typography.titleMedium)
                            Text(b.summary, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
