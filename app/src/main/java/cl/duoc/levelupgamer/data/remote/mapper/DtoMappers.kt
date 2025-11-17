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
    nombre = nombre,
    apellido = apellido,
    email = email,
    rol = rol,
    fechaNacimiento = fechaNacimiento.orEmpty(),
    fotoPerfilUrl = fotoPerfilUrl,
    direccion = direccion,
    telefono = telefono,
    puntos = puntos ?: 0
)

fun ProductoDto.toEntity(): Producto = Producto(
    id = id,
    nombre = nombre,
    descripcion = descripcion,
    precio = precio,
    imageUrl = imageUrl ?: "",
    categoria = categoria.orEmpty(),
    codigo = codigo.orEmpty(),
    stock = stock ?: 0,
    descuento = descuento
)

fun CarritoDto.toEntities(): List<CarritoItemEntity> = items.map { item ->
    CarritoItemEntity(
        id = item.id,
        usuarioId = userId,
        productoId = item.productId,
        cantidad = item.quantity,
        unitPrice = item.unitPrice ?: 0.0
    )
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
