package cl.duoc.levelupgamer.model.local

import cl.duoc.levelupgamer.model.Producto

fun CarritoItemEntity.toProductoSnapshot(): Producto {
    val safeName = nombre.ifBlank { "Producto #$productoId" }
    val safeDescription = descripcion.ifBlank { safeName }
    val safeImage = imageUrl.ifBlank { "" }
    val price = if (unitPrice > 0.0) unitPrice else 0.0
    return Producto(
        id = productoId,
        nombre = safeName,
        descripcion = safeDescription,
        precio = price,
        imageUrl = safeImage,
        categoria = categoria,
        codigo = codigo,
        stock = 0,
        descuento = null,
        gallery = gallery
    )
}
