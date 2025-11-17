# LevelUpGamer - Aplicación Móvil

Aplicación Android (Jetpack Compose + MVVM) para la tienda LevelUpGamer. El cliente móvil consume el backend REST oficial: maneja autenticación JWT, sincroniza catálogo/carrito, envía pedidos reales y refleja el balance de puntos de fidelización del usuario.

## Funcionalidades Clave

- **Autenticación segura:** Registro, login y refresco de tokens con almacenamiento cifrado (`EncryptedSharedPreferences`). La sesión se restaura automáticamente al abrir la app.
- **Catálogo sincronizado:** Productos y detalles se obtienen desde `/api/products` y se cachean en Room para navegación offline básica.
- **Carrito en línea + checkout:** Cada operación (agregar, actualizar, eliminar) impacta el carrito del backend. El botón *Finalizar compra* crea un pedido vía `/api/orders`, limpia el carrito y dispara la notificación local.
- **Perfil y puntos:** El perfil usa `/api/users/{id}` y muestra los puntos acumulados. Tras cada compra la app fuerza `refreshPerfil()` para reflejar los nuevos puntos.
- **Notificaciones y experiencia Compose:** UI Material 3, navegación declarativa y notificación local cuando un pedido se confirma.

## Configuración del Backend

La app lee la URL base desde `BuildConfig.API_BASE_URL`. Por defecto apunta a `http://98.89.104.110:8081/` (localhost expuesto al emulador). Puedes sobrescribirla añadiendo a `local.properties` o ejecutando Gradle con la propiedad `LEVELUP_API_URL`:

```properties
LEVELUP_API_URL=https://mi-backend.example.com/
```

> Incluye la barra final y asegúrate de que el backend exponga los endpoints descritos en `docs/Documentación del Backend.md`.

## Ejecución

1. **Clonar:**

   ```bash
   git clone https://github.com/PyKydo/LevelUpGamer-Mobile.git
   ```

2. **Abrir en Android Studio** (Hedgehog o superior) y sincronizar Gradle.
3. **Configurar la URL** si no usarás `10.0.2.2`.
4. **Ejecutar la app** en un emulador/dispositivo (Android 8.0+). El primer arranque descargará dependencias desde Maven Central.

## Testing

Ejecuta las pruebas unitarias (Kotest + JUnit5 + MockK) desde el proyecto raíz:

```bash
./gradlew test        # macOS/Linux
.\gradlew.bat test    # Windows
```

> La primera ejecución puede tardar por la descarga de `retrofit:2.11.0` y otras dependencias. Si falla por timeout vuelve a lanzar el comando.

## Integrantes

- Matías Gutiérrez
- Víctor Mena
- David Larenas
