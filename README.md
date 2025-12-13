# LevelUpGamer - Aplicación Android

## Nombres de los Integrantes

- Matías Gutiérrez
- David Larenas
- Víctor Mena

## Funcionalidades

- Autenticación, registro y cambio de contraseña con tokens seguros almacenados localmente.
- Catálogo, blogs y destacados sincronizados con el backend y persistidos en Room para lectura offline.
- Carrito local persistente con edición de ítems, totales en vivo y respaldo por usuario.
- Checkout integrado al backend: crea pedidos reales, dispara notificaciones y limpia el carrito.
- Visualización de puntos y estado básico de pedidos con avisos cuando cambian de etapa.

## Endpoints Usados (Propios y Externos)

- Propios: `/api/v1/auth/login`, `/api/v1/users/register`, `/api/v1/auth/change-password`, `/api/v1/products`, `/api/v1/products/{id}`, `/api/v1/products/featured`, `/api/v1/blog-posts`, `/api/v1/orders`, `/api/v1/cart`.
- Externos: AWS S3 (imágenes/markdown), Picsum (imágenes fallback) y LocalStack como mock opcional.

## Instrucciones para Ejecutar el Proyecto

1. `git clone https://github.com/PyKydo/LevelUpGamer-Mobile.git`
2. Abrir en Android Studio y sincronizar Gradle.
3. Seleccionar variante:
   - `remoteDebug` (predeterminada): consume backend desplegado; se puede sobreescribir con `apiUrl`.
   - `localDebug`: apunta a `http://10.0.2.2:8081/`; ajustar `localApiUrl` si se usa dispositivo físico/IP distinta.
4. Definir `apiUrl`/`localApiUrl` en `local.properties` o pasando `-PapiUrl=` / `-PlocalApiUrl=`.
5. Conceder permisos de cámara/ubicación al ejecutar y lanzar en emulador/dispositivo Android 8+.

## APK Firmado y Archivo `.jks`

- APK firmado disponible en Releases (placeholder hasta publicación): <https://github.com/PyKydo/LevelUpGamer-Mobile/releases/latest>
- Binarios publicados en la Release:
- **app-remote-release.apk**: build firmada apuntando al backend remoto desplegado.
- **app-local-release.apk**: build firmada para backend local (`10.0.2.2:8081`), pensada para pruebas en red interna.
- **app-remote-debug.apk**: build de depuración contra el backend remoto, incluye logs y herramientas de QA.
- **app-local-debug.apk**: build de depuración para backend local, facilita probar servicios en tu equipo.
- Keystore local: `LevelUpGamer/keystore/levelup.jks` (fuera del repositorio; protegido mediante `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`).
- Generado con `keytool -genkeypair` y reutilizado por la tarea `assembleRelease`.

## Código Fuente de Microservicios y App Móvil

- Backend / microservicios: <https://github.com/PyKydo/LevelUpGamer-Backend>
- App móvil (este repositorio): estructura modular en `app/src/main/java/com/levelupgamer/...`

## Evidencia de Trabajo Colaborativo

![Evidencia de colaboración](evidencia_colaboracion.png)

## Información Técnica Complementaria

- **Arquitectura:** Jetpack Compose + ViewModel + StateFlow
- **Red:** Retrofit + OkHttp con interceptores
- **Persistencia:** Room (catálogo/carrito) y DataStore (preferencias y tokens).
- **Inyección:** `ServiceLocator` por variante con inicialización perezosa.
- **Pruebas:** `./gradlew test` para unitarias.
- **Observaciones:** catálogo/blogs cacheados localmente; próximas iteraciones refuerzan pruebas instrumentadas y ajustes visuales.
