package cl.duoc.levelupgamer.data.remote.mapper

import cl.duoc.levelupgamer.data.remote.dto.carrito.CarritoDto
import cl.duoc.levelupgamer.data.remote.dto.carrito.CarritoItemDto
import cl.duoc.levelupgamer.data.remote.dto.gamificacion.PuntosDto
import cl.duoc.levelupgamer.data.remote.dto.productos.ProductoDto
import cl.duoc.levelupgamer.data.remote.dto.users.UsuarioRespuestaDto
import cl.duoc.levelupgamer.model.Producto
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

@Suppress("unused")
class DtoMappersTest : StringSpec({

    "UsuarioRespuestaDto se mapea correctamente a Usuario de dominio" {
        val usuarioDto = UsuarioRespuestaDto(
            id = 123L,
            run = "11.111.111-1",
            nombre = "Juanito",
            apellidos = "Perez",
            correo = "juanito.perez@test.com",
            rol = "CLIENTE",
            fechaNacimiento = "25/12/1995",
            fotoPerfilUrl = "http://example.com/foto.jpg",
            region = "RM",
            comuna = "Santiago",
            direccion = "Av. Siempre Viva 742",
            telefono = "+56912345678",
            puntos = 1500
        )

        val usuarioDomain = usuarioDto.toDomain()

        usuarioDomain.id shouldBe usuarioDto.id
        usuarioDomain.run shouldBe usuarioDto.run
        usuarioDomain.nombre shouldBe usuarioDto.nombre
        usuarioDomain.apellido shouldBe usuarioDto.apellidos
        usuarioDomain.email shouldBe usuarioDto.correo
        usuarioDomain.rol shouldBe usuarioDto.rol
        usuarioDomain.fechaNacimiento shouldBe usuarioDto.fechaNacimiento
        usuarioDomain.fotoPerfilUrl shouldBe usuarioDto.fotoPerfilUrl
        usuarioDomain.region shouldBe usuarioDto.region
        usuarioDomain.comuna shouldBe usuarioDto.comuna
        usuarioDomain.direccion shouldBe usuarioDto.direccion
        usuarioDomain.telefono shouldBe usuarioDto.telefono
        usuarioDomain.puntos shouldBe usuarioDto.puntos
    }

    "UsuarioRespuestaDto con campos nulos se mapea con valores por defecto" {
        val usuarioDto = UsuarioRespuestaDto(
            id = 456L,
            nombre = "Ana",
            correo = "ana@test.com",
            apellidos = null,
            rol = null,
            fechaNacimiento = null,
            fotoPerfilUrl = null,
            region = null,
            comuna = null,
            direccion = null,
            telefono = null,
            puntos = null
        )

        val usuarioDomain = usuarioDto.toDomain()


        usuarioDomain.id shouldBe usuarioDto.id
        usuarioDomain.nombre shouldBe usuarioDto.nombre
        usuarioDomain.email shouldBe usuarioDto.correo
        usuarioDomain.apellido shouldBe null
        usuarioDomain.rol shouldBe null
        usuarioDomain.fechaNacimiento shouldBe ""
        usuarioDomain.fotoPerfilUrl shouldBe null
        usuarioDomain.region shouldBe null
        usuarioDomain.comuna shouldBe null
        usuarioDomain.direccion shouldBe null
        usuarioDomain.telefono shouldBe null
        usuarioDomain.puntos shouldBe 0
    }

    "CarritoDto asigna el usuario de respaldo cuando el backend responde como invitado" {
        val carritoDto = CarritoDto(
            userId = 0,
            items = listOf(
                CarritoItemDto(
                    id = 1,
                    productId = 9,
                    quantity = 2,
                    unitPrice = 2990.0,
                    product = ProductoDto(
                        id = 9,
                        nombre = "Control",
                        descripcion = "Inalambrico",
                        precio = BigDecimal.valueOf(2990)
                    )
                )
            )
        )

        val entities = carritoDto.toEntities(fallbackUserId = 77)

        entities.first().usuarioId shouldBe 77
        entities.first().nombre shouldBe "Control"
        entities.first().unitPrice shouldBe 2990.0
    }

    "CarritoDto usa el resolutor local cuando no llega snapshot del producto" {
        val dto = CarritoDto(
            userId = 5,
            items = listOf(
                CarritoItemDto(
                    id = 2,
                    productId = 44,
                    quantity = 1,
                    unitPrice = null,
                    product = null
                )
            )
        )
        val fallback = Producto(
            id = 44,
            nombre = "Teclado",
            descripcion = "Mecanico",
            precio = 15990.0,
            imageUrl = "/img.png",
            categoria = "ACCESORIOS",
            codigo = "KB-1",
            stock = 10,
            gallery = listOf("/img.png", "/img2.png")
        )

        val entities = dto.toEntities(
            productoResolver = { if (it == fallback.id) fallback else null }
        )

        val entity = entities.first()
        entity.nombre shouldBe fallback.nombre
        entity.unitPrice shouldBe fallback.precio
        entity.gallery shouldBe fallback.gallery
    }

    "PuntosDto se convierte en PuntosBalance" {
        val dto = PuntosDto(userId = 77, puntos = 1500, motivo = "compra")

        val balance = dto.toDomain()

        balance.userId shouldBe 77
        balance.puntos shouldBe 1500
        balance.motivo shouldBe "compra"
    }

    "ProductoDto normaliza rutas relativas y absolutas" {
        val dto = ProductoDto(
            id = 10,
            nombre = "Set",
            codigo = "SET-1",
            descripcion = "",
            imagenes = listOf("   ", "folder/photo.png", "/already.png", "http://cdn/img.png", "avatar.png")
        )

        val entity = dto.toEntity()

        entity.imageUrl shouldBe "/folder/photo.png"
        entity.gallery shouldBe listOf(
            "/folder/photo.png",
            "/already.png",
            "http://cdn/img.png",
            "avatar.png"
        )
    }
})