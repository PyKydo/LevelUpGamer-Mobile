package cl.duoc.levelupgamer.ui

import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import cl.duoc.levelupgamer.BuildConfig
import cl.duoc.levelupgamer.model.Blog
import cl.duoc.levelupgamer.viewmodel.BlogDetailViewModel
import androidx.core.text.HtmlCompat
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogDetailScreen(
    blog: Blog,
    detailVm: BlogDetailViewModel,
    onBack: () -> Unit
) {
    val loading by detailVm.loading.collectAsState()
    val contentText by detailVm.content.collectAsState()
    val error by detailVm.error.collectAsState()

    LaunchedEffect(blog.id) {
        detailVm.loadBlogContent(blog)
    }

    val parser = remember { Parser.builder().build() }
    val renderer = remember { HtmlRenderer.builder().build() }
    val renderedHtml = remember(contentText) {
        contentText?.let { renderer.render(parser.parse(it)) } ?: ""
    }


    val onBackgroundColorArgb = MaterialTheme.colorScheme.onBackground.toArgb()

    val imageModel = remember(blog.imageUrl) {
        blog.imageUrl?.takeIf { it.isNotBlank() }?.let { url ->
            when {
                url.startsWith("http", ignoreCase = true) -> url
                url.startsWith("/") -> BuildConfig.API_BASE_URL.trimEnd('/') + url
                else -> null
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(blog.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .verticalScroll(rememberScrollState())
        ) {
            if (imageModel != null) {
                AsyncImage(
                    model = imageModel,
                    contentDescription = blog.altImage ?: "Imagen del blog",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = cl.duoc.levelupgamer.R.drawable.logo),
                    error = painterResource(id = cl.duoc.levelupgamer.R.drawable.logo)
                )
            } else {
                Image(
                    painter = painterResource(id = cl.duoc.levelupgamer.R.drawable.logo),
                    contentDescription = blog.altImage ?: "Imagen del blog",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(blog.title, style = MaterialTheme.typography.headlineSmall)
                if (blog.summary.isNotBlank()) {
                    Text(blog.summary, style = MaterialTheme.typography.bodyMedium)
                }

                when {
                    loading -> {
                        Spacer(modifier = Modifier.height(4.dp))
                        CircularProgressIndicator()
                    }
                    error != null -> {
                        Text(
                            text = error ?: "Error desconocido",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    !contentText.isNullOrBlank() -> {
                        AndroidView(
                            factory = { context ->
                                TextView(context).apply {
                                    textSize = 16f
                                    setTextColor(onBackgroundColorArgb)
                                    movementMethod = LinkMovementMethod.getInstance()
                                }
                            },
                            update = { textView ->
                                textView.text = HtmlCompat.fromHtml(renderedHtml, HtmlCompat.FROM_HTML_MODE_LEGACY)
                            },
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                    else -> {
                        Text(
                            text = "Sin contenido disponible",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
