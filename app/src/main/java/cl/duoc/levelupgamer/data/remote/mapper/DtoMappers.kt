package cl.duoc.levelupgamer.data.remote.mapper

import cl.duoc.levelupgamer.data.remote.dto.carrito.CarritoDto
import cl.duoc.levelupgamer.data.remote.dto.gamificacion.PuntosDto
import cl.duoc.levelupgamer.data.remote.dto.pedidos.PedidoProductoDto
import cl.duoc.levelupgamer.data.remote.dto.pedidos.PedidoRespuestaDto
import cl.duoc.levelupgamer.data.remote.dto.productos.ProductoDto
import cl.duoc.levelupgamer.data.remote.dto.users.UsuarioRespuestaDto
import cl.duoc.levelupgamer.model.Pedido
import cl.duoc.levelupgamer.model.PedidoItem
import cl.duoc.levelupgamer.model.Producto
import cl.duoc.levelupgamer.model.PuntosBalance
import cl.duoc.levelupgamer.model.Usuario
import cl.duoc.levelupgamer.model.local.CarritoItemEntity

fun UsuarioRespuestaDto.toDomain(): Usuario = Usuario(
    id = id,
    run = run,
    nombre = nombre,
    apellido = apellidos,
    email = correo,
    rol = rol,
    fechaNacimiento = fechaNacimiento.orEmpty(),
    fotoPerfilUrl = fotoPerfilUrl,
    region = region,
    comuna = comuna,
    direccion = direccion,
    telefono = telefono,
    puntos = puntos ?: 0
)

fun ProductoDto.toEntity(): Producto {
    val resolvedName = nombre?.takeIf { it.isNotBlank() } ?: codigo.orEmpty()
    val resolvedDescription = descripcion?.takeIf { it.isNotBlank() } ?: resolvedName
    return Producto(
        id = id,
        nombre = resolvedName,
        descripcion = resolvedDescription,
        precio = precio?.toDouble() ?: 0.0,
        imageUrl = resolvePrimaryImageUrl(),
        categoria = categoria?.nombre.orEmpty(),
        codigo = codigo.orEmpty(),
        stock = stock ?: 0,
        descuento = descuento,
        gallery = resolveGallery()
    )
}

fun CarritoDto.toEntities(
    fallbackUserId: Long = userId,
    productoResolver: (Long) -> Producto? = { null }
): List<CarritoItemEntity> {
    val ownerId = if (userId != 0L) userId else fallbackUserId
    return items.map { item ->
        val productoSnapshot = item.product?.toEntity() ?: productoResolver(item.productId)
        CarritoItemEntity(
            id = item.id,
            usuarioId = ownerId,
            productoId = item.productId,
            cantidad = item.quantity,
            unitPrice = item.unitPrice ?: productoSnapshot?.precio ?: 0.0,
            nombre = productoSnapshot?.nombre.orEmpty(),
            descripcion = productoSnapshot?.descripcion.orEmpty(),
            codigo = productoSnapshot?.codigo.orEmpty(),
            imageUrl = productoSnapshot?.imageUrl.orEmpty(),
            categoria = productoSnapshot?.categoria.orEmpty(),
            gallery = productoSnapshot?.gallery ?: emptyList()
        )
    }
}

fun PedidoRespuestaDto.toDomain(): Pedido = Pedido(
    id = id,
    userId = userId,
    total = total,
    estado = estado,
    items = items.map { it.toDomain() }
)

private fun PedidoProductoDto.toDomain(): PedidoItem = PedidoItem(
    productoId = productoId,
    nombre = nombre,
    cantidad = cantidad,
    precioUnitario = precioUnitario
)

fun PuntosDto.toDomain(): PuntosBalance = PuntosBalance(
    userId = userId,
    puntos = puntos,
    motivo = motivo
)

private fun ProductoDto.resolvePrimaryImageUrl(): String {
    val legacy = normalizeRemotePath(imageUrl)
    if (legacy.isNotEmpty()) {
        return legacy
    }
    val fromList = imagenes?.firstOrNull { it.isNotBlank() }
    val normalized = normalizeRemotePath(fromList)
    return normalized
}

private fun ProductoDto.resolveGallery(): List<String> {
    return imagenes
        ?.mapNotNull { normalizeRemotePath(it).takeIf { path -> path.isNotEmpty() } }
        ?.distinct()
        ?: emptyList()
}

private fun normalizeRemotePath(raw: String?): String {
    val value = raw?.trim().orEmpty()
    if (value.isEmpty()) {
        return ""
    }
    if (value.startsWith("http", ignoreCase = true) || value.startsWith("/")) {
        return value
    }
    return if (value.contains('/')) {
        "/$value"
    } else {
        value
    }
}
