package cl.duoc.levelupgamer.data.remote.mapper

import cl.duoc.levelupgamer.data.remote.dto.users.UsuarioRespuestaDto
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

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
})