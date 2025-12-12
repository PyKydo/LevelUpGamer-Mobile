package cl.duoc.levelupgamer.ui

import android.util.Base64
import android.view.ViewGroup
import android.webkit.WebView
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
import androidx.compose.material3.ColorScheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import cl.duoc.levelupgamer.BuildConfig
import cl.duoc.levelupgamer.model.Blog
import cl.duoc.levelupgamer.viewmodel.BlogDetailViewModel
import androidx.core.graphics.ColorUtils
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import cl.duoc.levelupgamer.R

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
    val context = LocalContext.current

    LaunchedEffect(blog.id) {
        detailVm.loadBlogContent(blog)
    }

    val markdownExtensions = remember { listOf(TablesExtension.create()) }
    val parser = remember { Parser.builder().extensions(markdownExtensions).build() }
    val renderer = remember { HtmlRenderer.builder().extensions(markdownExtensions).build() }
    val renderedHtml = remember(contentText) {
        contentText?.let { renderer.render(parser.parse(it)) } ?: ""
    }

    val colorScheme = MaterialTheme.colorScheme
    val outfitFontBase64 = remember {
        runCatching {
            context.resources.openRawResource(R.font.outfit_regular).use { stream ->
                Base64.encodeToString(stream.readBytes(), Base64.NO_WRAP)
            }
        }.getOrNull()
    }
    val styledHtml = remember(renderedHtml, colorScheme, outfitFontBase64) {
        buildStyledHtml(renderedHtml, colorScheme, outfitFontBase64)
    }

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
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
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
                            factory = { ctx ->
                                WebView(ctx).apply {
                                    layoutParams = ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT
                                    )
                                    isVerticalScrollBarEnabled = false
                                    setBackgroundColor(colorScheme.background.toArgb())
                                    settings.apply {
                                        loadsImagesAutomatically = true
                                        javaScriptEnabled = false
                                        builtInZoomControls = false
                                        displayZoomControls = false
                                        defaultFontSize = 16
                                    }
                                }
                            },
                            update = { webView ->
                                webView.setBackgroundColor(colorScheme.background.toArgb())
                                if (webView.tag != styledHtml) {
                                    webView.loadDataWithBaseURL(null, styledHtml, "text/html", "utf-8", null)
                                    webView.tag = styledHtml
                                }
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

private fun buildStyledHtml(bodyHtml: String, colors: ColorScheme, fontBase64: String?): String {
    val safeBody = bodyHtml.ifBlank { "<p></p>" }
    val textColor = colors.onBackground.toCssHex()
    val backgroundColor = colors.background.toCssHex()
    val headingColor = colors.onBackground.toCssHex()
    val accentColor = colors.primary.toCssHex()
    val dividerColor = colors.outlineVariant.toCssHex()
    val rowStripeColor = blendToHex(colors.surfaceVariant, colors.background, 0.45f)
    val headerBackground = blendToHex(colors.surface, colors.primary, 0.08f)
    val fontFaceRule = fontBase64?.let {
        """
            @font-face {
                font-family: 'Outfit';
                src: url('data:font/ttf;base64,$it') format('truetype');
                font-weight: 400;
                font-style: normal;
            }
        """.trimIndent()
    } ?: ""

    val styles = """
        $fontFaceRule
        body {
            margin: 0;
            padding: 0;
            font-size: 16px;
            line-height: 1.7;
            color: $textColor;
            background-color: $backgroundColor;
            font-family: 'Outfit', 'Inter', 'Roboto', 'Helvetica Neue', sans-serif;
        }
        main {
            padding: 0 4px 24px 4px;
        }
        h1, h2, h3, h4, h5, h6 {
            color: $headingColor;
            margin-top: 1.6rem;
            margin-bottom: 0.6rem;
            line-height: 1.3;
        }
        p {
            margin-bottom: 1.05rem;
        }
        ul, ol {
            padding-left: 1.4rem;
            margin: 0 0 1.1rem 0;
        }
        li {
            margin-bottom: 0.45rem;
            padding-left: 0.2rem;
        }
        li::marker {
            color: $accentColor;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin: 1.25rem 0;
            font-size: 0.95rem;
            display: block;
            overflow-x: auto;
            -webkit-overflow-scrolling: touch;
            border-radius: 12px;
        }
        th, td {
            border: 1px solid $dividerColor;
            padding: 0.65rem 0.6rem;
            text-align: left;
            min-width: 120px;
        }
        th {
            background-color: $headerBackground;
            font-weight: 600;
        }
        tr:nth-child(even) td {
            background-color: $rowStripeColor;
        }
        table::-webkit-scrollbar {
            height: 6px;
        }
        table::-webkit-scrollbar-thumb {
            background-color: $dividerColor;
            border-radius: 3px;
        }
        blockquote {
            border-left: 4px solid $accentColor;
            padding: 1rem 1.25rem;
            margin: 1.4rem 0;
            color: $textColor;
            background-color: $rowStripeColor;
            border-radius: 16px;
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.07);
        }
        code {
            background-color: $rowStripeColor;
            padding: 0.15rem 0.35rem;
            border-radius: 6px;
            font-family: 'JetBrains Mono', 'Courier New', monospace;
        }
        img {
            max-width: 100%;
            height: auto;
            border-radius: 12px;
            margin: 1rem 0;
        }
        a {
            color: $accentColor;
            text-decoration: none;
            font-weight: 600;
        }
        a:hover {
            text-decoration: underline;
        }
    """.trimIndent()

    return """
        <html>
          <head>
            <meta charset=\"utf-8\"/>
            <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"/>
            <style>$styles</style>
          </head>
          <body>
            <main>$safeBody</main>
          </body>
        </html>
    """.trimIndent()
}

private fun Color.toCssHex(): String {
    val argb = this.toArgb()
    return String.format("#%06X", 0xFFFFFF and argb)
}

private fun blendToHex(first: Color, second: Color, ratio: Float): String {
    val blendedArgb = ColorUtils.blendARGB(first.toArgb(), second.toArgb(), ratio.coerceIn(0f, 1f))
    return String.format("#%06X", 0xFFFFFF and blendedArgb)
}
