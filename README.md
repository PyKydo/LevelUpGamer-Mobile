# LevelUpGamer - App

Esto es una app Android hecha con Jetpack Compose. Es un cliente para la tienda LevelUpGamer.

Funciona con el backend que tenemos y guarda cosas en local cuando hace falta.

## Qué hace

- Login y registro (tokens guardados de forma segura).
- Muestra productos (los baja del backend y los guarda en Room).
- Carrito local: puedes agregar, cambiar cantidad y borrar, todo se guarda en la app.
- Hacer checkout crea un pedido en el backend y limpia el carrito.
- Muestra puntos del usuario y una notificación cuando el pedido queda listo.

## Cosas técnicas

- UI: Jetpack Compose + ViewModel.
- Red: Retrofit + OkHttp.
- Almacenamiento: Room (productos + carrito).
- Inyección simple: `ServiceLocator`.

## Cómo correr

1. Clona el repo:

    ```bash
    git clone https://github.com/PyKydo/LevelUpGamer-Mobile.git
    ```

2. Abre el proyecto en Android Studio y sincroniza Gradle.
3. Elige el backend que necesitas desde las variantes de build:
    - `remoteDebug` (por defecto): usa el backend desplegado y lee la propiedad `apiUrl` si está definida.
    - `localDebug`: apunta a `http://10.0.2.2:8081/` (loopback del emulador). Ajusta la propiedad `localApiUrl` si expones el backend con otra IP/puerto o si corres en un dispositivo físico (usa la IP de tu PC en la red local).

    Puedes definir estas propiedades en `local.properties` o pasar `-PapiUrl=` / `-PlocalApiUrl=` al invocar Gradle.

4. Ejecuta en un emulador o dispositivo (Android 8+).

## Tests (si quieres)

Para pruebas unitarias en la compu:

```bash
./gradlew test
```

## Notas y cosas por mejorar (lista rápida)

- El carrito ahora es local por defecto (mejor para cuando el backend falla).
- La UI es simple; hay pantallas que se pueden pulir más (colores, espaciado).

## Equipo

- Matías
- Víctor
- David
