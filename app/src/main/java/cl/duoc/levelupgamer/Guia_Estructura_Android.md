# Guía de Estructura y Convenciones para Proyecto Android (Referencia: ActGuía12)

## 1. Estructura de Paquetes y Archivos

- **Organización:** Por capas (no por features):
  - `ui/` (pantallas, temas)
  - `viewmodel/` (ViewModels)
  - `model/` (modelos de dominio)
  - `data/` (repositorios, fuentes de datos)
  - `util/` (utilidades)
- **Nomenclatura:**
  - Paquetes: minúsculas, sin guiones.
  - Clases: PascalCase (`MainActivity`, `EstadoViewModel`).
  - Variables: camelCase.
- **Profundidad:** Múltiples niveles, ejemplo: `com.example.app.ui.theme`.
- **Relación con MVVM:**
  - Separación clara entre UI, lógica de presentación y persistencia.

## 2. Arquitectura y Patrones

- **MVVM:** ViewModels en `viewmodel/`, heredan de `AndroidViewModel`.
- **Inyección de dependencias:** Manual (recomendado Hilt para proyectos grandes).
- **Comunicación View-ViewModel:** `StateFlow` y `MutableStateFlow`.
- **Manejo de estado:** Uso de `StateFlow<Boolean?>` para estados nulos/iniciales.
- **Navegación:** `androidx.navigation:navigation-compose`.

## 3. UI/UX y Jetpack Compose

- **@Composable:** Funciones composables en `ui/`.
- **Custom Composables:** Ejemplo: `PantallaPrincipal`.
- **Manejo de estado en UI:** `remember`, `mutableStateOf` (estándar en Compose).
- **Tema:** Material 3 (`material3`).
- **Custom theme:**
  - Colores (`Color.kt`)
  - Tipografía (`Type.kt`)
  - Tema (`Theme.kt`)
- **Modo oscuro/claro:** Implementado con `isSystemInDarkTheme()` y dynamic color.
- **Recursos:** `strings.xml`, `colors.xml`.

## 4. Capa de Datos y Persistencia

- **Repository:** No implementado explícitamente, pero recomendable.
- **Fuentes de datos:** DataStore Preferences (`EstadoDataStore`).
- **Room/Retrofit:** No presente, pero sugerido para proyectos con BD o red.

## 5. Configuración Gradle y Build

- **Kotlin DSL:** Uso de `build.gradle.kts`.
- **Version catalog:** Uso de `libs.versions.toml`.
- **Plugins:**
  - `android.application`
  - `kotlin.android`
  - `kotlin.compose`
- **Dependencias clave:**
  - Compose Material 3
  - Navigation Compose
  - Lifecycle ViewModel Compose
  - DataStore Preferences

## 6. Convenciones de Código

- **Nombres:**
  - Clases: PascalCase
  - Variables: camelCase
  - Paquetes: minúsculas
- **Un archivo por clase principal**
- **Indentación:** 4 espacios
- **Imports:** Específicos
- **Documentación:** KDoc recomendado

## 7. Patrones Android Específicos

- **Lifecycle-aware:** Uso de `AndroidViewModel`, `viewModelScope`.
- **Coroutines:** Uso de `viewModelScope.launch`, `delay`.

## 8. Archivos de Configuración

- **ProGuard/R8:** Archivo `proguard-rules.pro` presente.
- **AndroidManifest.xml:** Configuración estándar, define actividad principal y tema.

## 9. Buenas Prácticas y Seguridad

- **Errores:** Manejo de estados nulos, pero se recomienda patrón Loading/Success/Error.
- **Performance Compose:** Uso de Scaffold, temas y estructura eficiente.

## 10. Estructura de Carpetas Recomendada

```
app/src/main/java/com/tuapp/
  ├── ui/screens/
  ├── ui/components/
  ├── ui/navigation/
  ├── data/repository/
  ├── data/database/
  ├── model/
  ├── viewmodel/
  └── util/
```

---

## Ejemplos de Implementación

**ViewModel con StateFlow:**
```kotlin
class EstadoViewModel(application: Application): AndroidViewModel(application) {
    private val _activo = MutableStateFlow<Boolean?>(null)
    val activo: StateFlow<Boolean?> = _activo
    // ...lógica...
}
```

**Theme personalizado:**
```kotlin
@Composable
fun MyAppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
```

**Gradle (app/build.gradle.kts):**
```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}
android {
    // ...configuración...
    buildFeatures { compose = true }
}
dependencies {
    implementation("androidx.navigation:navigation-compose:2.9.5")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")
    implementation("androidx.datastore:datastore-preferences:1.1.7")
    // ...otras dependencias...
}
```

---

## Resumen

El proyecto de referencia es limpio, modular y sigue buenas prácticas de Android moderno con Jetpack Compose y MVVM. Replica la estructura de capas, el uso de StateFlow, Compose Material 3 y la configuración Gradle con Kotlin DSL y version catalog para asegurar escalabilidad y mantenibilidad en tu proyecto Level-Up Gamer.
