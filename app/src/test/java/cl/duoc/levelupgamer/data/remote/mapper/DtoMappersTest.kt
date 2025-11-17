package cl.duoc.levelupgamer.data.remote.mapper

import cl.duoc.levelupgamer.data.remote.dto.users.UsuarioRespuestaDto
import com.google.common.truth.Truth.assertThat
import io.kotest.core.spec.style.StringSpec

class DtoMappersTest : StringSpec({

    "UsuarioRespuestaDto se mapea correctamente a Usuario de dominio" {
        val usuarioDto = UsuarioRespuestaDto(
            id = 123L,
            nombre = "Juanito",
            apellido = "Perez",
            email = "juanito.perez@test.com",
            rol = "CLIENTE",
            fechaNacimiento = "25/12/1995",
            fotoPerfilUrl = "http://example.com/foto.jpg",
            direccion = "Av. Siempre Viva 742",
            telefono = "+56912345678",
            puntos = 1500
        )

        val usuarioDomain = usuarioDto.toDomain()

        assertThat(usuarioDomain.id).isEqualTo(usuarioDto.id)
        assertThat(usuarioDomain.nombre).isEqualTo(usuarioDto.nombre)
        assertThat(usuarioDomain.apellido).isEqualTo(usuarioDto.apellido)
        assertThat(usuarioDomain.email).isEqualTo(usuarioDto.email)
        assertThat(usuarioDomain.rol).isEqualTo(usuarioDto.rol)
        assertThat(usuarioDomain.fechaNacimiento).isEqualTo(usuarioDto.fechaNacimiento)
        assertThat(usuarioDomain.fotoPerfilUrl).isEqualTo(usuarioDto.fotoPerfilUrl)
        assertThat(usuarioDomain.direccion).isEqualTo(usuarioDto.direccion)
        assertThat(usuarioDomain.telefono).isEqualTo(usuarioDto.telefono)
        assertThat(usuarioDomain.puntos).isEqualTo(usuarioDto.puntos)
    }

    "UsuarioRespuestaDto con campos nulos se mapea con valores por defecto" {
        val usuarioDto = UsuarioRespuestaDto(
            id = 456L,
            nombre = "Ana",
            email = "ana@test.com",
            apellido = null,
            rol = null,
            fechaNacimiento = null,
            fotoPerfilUrl = null,
            direccion = null,
            telefono = null,
            puntos = null
        )

        val usuarioDomain = usuarioDto.toDomain()


        assertThat(usuarioDomain.id).isEqualTo(usuarioDto.id)
        assertThat(usuarioDomain.nombre).isEqualTo(usuarioDto.nombre)
        assertThat(usuarioDomain.email).isEqualTo(usuarioDto.email)
        assertThat(usuarioDomain.apellido).isNull()
        assertThat(usuarioDomain.rol).isNull()
        assertThat(usuarioDomain.fechaNacimiento).isEmpty()
        assertThat(usuarioDomain.fotoPerfilUrl).isNull()
        assertThat(usuarioDomain.direccion).isNull()
        assertThat(usuarioDomain.telefono).isNull()
        assertThat(usuarioDomain.puntos).isEqualTo(0)
    }
})