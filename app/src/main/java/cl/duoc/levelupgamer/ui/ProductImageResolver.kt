package cl.duoc.levelupgamer.ui

import android.content.Context
import cl.duoc.levelupgamer.R
import cl.duoc.levelupgamer.model.Producto
import java.util.Locale

fun resolveProductImageResId(context: Context, producto: Producto): Int {
    val candidates = mutableListOf<String>()

    val explicit = producto.imageUrl.trim()
    if (explicit.isNotEmpty()) {
        val cleaned = explicit.cleanResourceName()
        val lower = cleaned.lowercase(Locale.ROOT)
        candidates += explicit
        candidates += cleaned
        candidates += lower
        candidates += lower.replace('-', '_')
    }

    val codePrefix = producto.codigo.take(5).lowercase(Locale.ROOT)
    if (codePrefix.isNotEmpty()) {
        candidates += "products_$codePrefix"
        candidates += "products${codePrefix}"
        candidates += codePrefix
    }

    candidates.distinctBy { it.lowercase(Locale.ROOT) }
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .forEach { candidate ->
            val resId = context.resources.getIdentifier(candidate, "drawable", context.packageName)
            if (resId != 0) {
                return resId
            }
        }

    findDrawableByCodePrefix(codePrefix)?.let { return it }

    return R.drawable.logo
}

private fun String.cleanResourceName(): String {
    val noPrefix = removePrefix("res/").removePrefix("drawable/")
    val name = noPrefix.substringAfterLast('/')
    return name.substringBeforeLast('.').ifEmpty { name }
}

private fun findDrawableByCodePrefix(codePrefix: String): Int? {
    if (codePrefix.isEmpty()) return null
    val target = codePrefix.lowercase(Locale.ROOT)
    return runCatching {
        R.drawable::class.java.fields
            .firstOrNull { field ->
                val name = field.name.lowercase(Locale.ROOT)
                name.contains(target)
            }
            ?.getInt(null)
    }.getOrNull()
}
