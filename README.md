# LevelUpGamer - App (versión simple)

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
3. Si tu backend no está en la URL por defecto, pon la tuya en `local.properties` como:

```properties
LEVELUP_API_URL=https://mi-backend.example.com/
```

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
