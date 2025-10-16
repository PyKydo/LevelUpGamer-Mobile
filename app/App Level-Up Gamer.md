# **App Level-Up Gamer**

Este documento es para el desarrollo de la aplicación móvil "Level-Up Gamer" como parte de la Evaluación Parcial 2, organizado por fases de desarrollo.

**Miembros del Equipo:**

* Matías Gutiérrez (Lógica y Desarrollo Complejo)  
* David Larenas (UI/UX y Diseño Visual)  
* Víctor Mena (Desarrollo General e Integración)

Stack de Tecnologías y Herramientas  
Este proyecto será desarrollado utilizando un stack moderno de tecnologías y herramientas para asegurar un desarrollo eficiente, colaborativo y de alta calidad.

* **IDE:** Android Studio (Entorno de Desarrollo Integrado oficial para Android).  
* **Lenguaje de Programación:** Kotlin (Lenguaje oficial para el desarrollo moderno de Android).  
* **UI Toolkit:** Jetpack Compose (Framework declarativo moderno para la construcción de interfaces de usuario nativas).  
* **Diseño Visual:** Material 3 (La última versión del sistema de diseño de Google).  
* **Control de Versiones:** Git y GitHub (Para la gestión del código fuente y la colaboración).  
* **Gestión de Proyecto:** Trello (Para la organización de tareas y seguimiento del progreso).

Configuración del Proyecto Base  
Para garantizar la consistencia en el entorno de desarrollo, el proyecto se configurará con las siguientes versiones de SDK y API.

* **compileSdk:** 34 (Android 14\)  
* **targetSdk:** 34 (Android 14\)  
* **minSdk:** 24 (Android 7.0 Nougat)

Estrategia de Ramas (Feature Branches)  
El proyecto utilizará el flujo de trabajo Feature Branch para mantener el código organizado y seguro. Cada rama se centrará en una característica funcional completa y aislada.

* **1\. feature/user-authentication**  
  * **Objetivo:** Implementar el flujo completo de registro e inicio de sesión de usuarios.  
  * **Tarjetas Relacionadas:** 4, 5, 6 (Fase 2).  
* **2\. feature/product-catalog**  
  * **Objetivo:** Desarrollar la visualización del catálogo de productos y el detalle de cada ítem.  
  * **Tarjetas Relacionadas:** 7, 8, 9 (parcialmente) (Fase 3).  
* **3\. feature/shopping-cart**  
  * **Objetivo:** Implementar la lógica y la interfaz para la gestión completa del carrito de compras.  
  * **Tarjetas Relacionadas:** 9 (parcialmente), 10 (parcialmente) (Fases 3 y 4).  
* **4\. feature/user-profile**  
  * **Objetivo:** Construir la pantalla de perfil del usuario, incluyendo la personalización con una foto de perfil.  
  * **Tarjetas Relacionadas:** 10 (parcialmente), 11 (Fase 4).  
* **5\. feature/checkout-simulation**  
  * **Objetivo:** Simular el proceso de compra final y proporcionar retroalimentación al usuario a través de notificaciones.  
  * **Tarjetas Relacionadas:** 12, 13 (Fase 4 y 5).

## **Fase 1: Fundación del Proyecto**

### **Tarjeta 1: Configuración Inicial del Proyecto y Arquitectura MVVM**

* **Título:** Configuración Inicial del Proyecto y Arquitectura MVVM  
* **Descripción:** Establecer la base del proyecto en Android Studio, incluyendo la configuración de dependencias (Material 3, Navigation-Compose, etc.) y la creación de la estructura de paquetes para la arquitectura MVVM.  
* **Categoría:** Arquitectura  
* **Checklist:**  
  * \[ \] Crear nuevo proyecto en Android Studio con Jetpack Compose.  
  * \[ \] Configurar build.gradle con las dependencias necesarias.  
  * \[ \] Definir la estructura de paquetes: ui, data, model, viewmodel, util.  
  * \[ \] Configurar el repositorio en GitHub y realizar el commit inicial.  
* **Miembro asignado:** Matías Gutiérrez

### **Tarjeta 2: Creación de Modelos de Datos (Entidades)**

* **Título:** Creación de Modelos de Datos (Entidades)  
* **Descripción:** Definir las data classes de Kotlin para las entidades principales de la aplicación: Usuario, Producto, ItemCarrito y Reseña.  
* **Categoría:** Arquitectura  
* **Checklist:**  
  * \[ \] Definir data class Usuario.  
  * \[ \] Definir data class Producto.  
  * \[ \] Definir data class ItemCarrito.  
  * \[ \] Definir data class Reseña.  
* **Miembro asignado:** Víctor Mena

### **Tarjeta 3: Diseño de Tema, Paleta de Colores y Tipografía (Material 3\)**

* **Título:** Diseño de Tema, Paleta de Colores y Tipografía (Material 3\)  
* **Descripción:** Implementar el tema visual de la aplicación en Jetpack Compose siguiendo las especificaciones de Material 3\. Se definirán los colores (negro, azul eléctrico, verde neón) y las fuentes (Roboto, Orbitron).  
* **Categoría:** UI/UX  
* **Checklist:**  
  * \[ \] Definir paleta de colores en el archivo Theme.kt.  
  * \[ \] Configurar el Typography.kt con las fuentes Roboto y Orbitron.  
  * \[ \] Aplicar tema para modo claro y oscuro.  
* **Miembro asignado:** David Larenas

## **Fase 2: Autenticación y Almacenamiento**

### **Tarjeta 4: Diseño de Pantallas de Autenticación (Login y Registro)**

* **Título:** Diseño de Pantallas de Autenticación (Login y Registro)  
* **Descripción:** Crear las pantallas de inicio de sesión y registro con Jetpack Compose. Deben incluir campos para email, contraseña y fecha de nacimiento, siguiendo la guía visual.  
* **Categoría:** UI/UX  
* **Checklist:**  
  * \[ \] Diseñar la screen de Login.  
  * \[ \] Diseñar la screen de Registro.  
  * \[ \] Incluir Composables para campos de texto, botones y logo.  
* **Miembro asignado:** David Larenas

### **Tarjeta 5: Implementación de la Lógica de Autenticación y Formularios Validados**

* **Título:** Implementación de la Lógica de Autenticación y Formularios Validados  
* **Descripción:** Desarrollar la lógica en el ViewModel para gestionar el registro y el inicio de sesión. Esto incluye validar los campos del formulario (email válido, contraseña segura, mayor de 18 años) y mostrar retroalimentación visual al usuario.  
* **Categoría:** Backend Formularios  
* **Checklist:**  
  * \[ \] Crear ViewModel para la autenticación.  
  * \[ \] Implementar la lógica de validación para cada campo.  
  * \[ \] Añadir la condición especial para correos @duoc.cl.  
  * \[ \] Gestionar el estado (loading, success, error) y comunicarlo a la UI.  
* **Miembro asignado:** Matías Gutiérrez

### **Tarjeta 6: Configuración del Almacenamiento Local (Base de Datos Room)**

* **Título:** Configuración del Almacenamiento Local (Base de Datos Room)  
* **Descripción:** Implementar la base de datos local usando Room para persistir los datos del usuario, el catálogo de productos y el carrito de compras.  
* **Categoría:** Base de Datos Almacenamiento Local  
* **Checklist:**  
  * \[ \] Añadir dependencia de Room.  
  * \[ \] Crear las entidades, DAOs (Data Access Objects) y la clase Database.  
  * \[ \] Implementar un Repositorio para abstraer el acceso a los datos.  
  * \[ \] Precargar la base de datos con los productos de ejemplo.  
* **Miembro asignado:** Matías Gutiérrez

## **Fase 3: Catálogo y Navegación Principal**

### **Tarjeta 7: Implementación de la Navegación Principal de la App**

* **Título:** Implementación de la Navegación Principal de la App  
* **Descripción:** Configurar el Navigation Component para Jetpack Compose para gestionar el flujo entre las pantallas: Splash \-\> Login/Registro \-\> Catálogo \-\> Detalle de Producto \-\> Carrito \-\> Perfil.  
* **Categoría:** Navegación  
* **Checklist:**  
  * \[ \] Configurar el NavHost con Navigation-Compose.  
  * \[ \] Definir las rutas y los destinos (@Composable screens).  
  * \[ \] Implementar las acciones de navegación entre destinos.  
* **Miembro asignado:** Víctor Mena

### **Tarjeta 8: Diseño del Catálogo de Productos y Vista de Detalle**

* **Título:** Diseño del Catálogo de Productos y Vista de Detalle  
* **Descripción:** Diseñar la interfaz para la lista de productos, utilizando un LazyColumn. También diseñar la pantalla de detalle que muestra la información completa de un producto seleccionado.  
* **Categoría:** UI/UX  
* **Checklist:**  
  * \[ \] Diseñar el Composable individual para un item de la lista.  
  * \[ \] Maquetar la screen del catálogo completo con LazyColumn.  
  * \[ \] Maquetar la screen de detalle del producto.  
* **Miembro asignado:** David Larenas

### **Tarjeta 9: Lógica y Gestión de Estado del Catálogo y Carrito**

* **Título:** Lógica y Gestión de Estado del Catálogo y Carrito  
* **Descripción:** Desarrollar los ViewModels para el catálogo de productos y el carrito de compras. La lógica debe permitir obtener los productos de la base de datos y gestionar las operaciones del carrito (agregar, eliminar, modificar cantidad).  
* **Categoría:** Backend Gestión de Estado  
* **Checklist:**  
  * \[ \] Crear ViewModel para el Catálogo.  
  * \[ \] Crear ViewModel para el Carrito.  
  * \[ \] Implementar función para agregar producto al carrito.  
  * \[ \] Implementar función para eliminar producto del carrito.  
  * \[ \] Implementar función para actualizar cantidad de un producto.  
* **Miembro asignado:** Matías Gutiérrez

## **Fase 4: Funcionalidades de Usuario y Recursos Nativos**

### **Tarjeta 10: Diseño de Interfaz del Perfil de Usuario y Carrito de Compras**

* **Título:** Diseño de Interfaz del Perfil de Usuario y Carrito de Compras  
* **Descripción:** Crear las pantallas para el perfil de usuario (donde se podrá actualizar información) y el carrito de compras (con el resumen de la compra) usando Jetpack Compose.  
* **Categoría:** UI/UX  
* **Checklist:**  
  * \[ \] Diseñar la screen de Perfil de Usuario.  
  * \[ \] Diseñar la screen del Carrito de Compras.  
  * \[ \] Diseñar el Composable individual para la lista del carrito.  
* **Miembro asignado:** David Larenas

### **Tarjeta 11: Integración de Recurso Nativo 1 (Cámara)**

* **Título:** Integración de Recurso Nativo 1 (Cámara)  
* **Descripción:** Implementar la funcionalidad para que el usuario pueda usar la cámara del dispositivo para tomar o seleccionar una foto para su perfil. Se deben gestionar los permisos correspondientes.  
* **Categoría:** Recursos Nativos  
* **Checklist:**  
  * \[ \] Solicitar permisos de cámara y almacenamiento.  
  * \[ \] Implementar Intent para abrir la cámara.  
  * \[ \] Gestionar el resultado y obtener la imagen.  
  * \[ \] Mostrar la imagen en el perfil del usuario.  
* **Miembro asignado:** Víctor Mena

### **Tarjeta 12: Integración de Recurso Nativo 2 (Notificaciones Locales)**

* **Título:** Integración de Recurso Nativo 2 (Notificaciones Locales)  
* **Descripción:** Implementar un sistema de notificaciones locales para informar al usuario cuando una compra (simulada) se haya completado exitosamente.  
* **Categoría:** Recursos Nativos  
* **Checklist:**  
  * \[ \] Crear un canal de notificaciones.  
  * \[ \] Construir la notificación (título, texto, ícono).  
  * \[ \] Implementar la lógica para disparar la notificación.  
* **Miembro asignado:** Víctor Mena

## **Fase 5: Finalización y Entrega**

### **Tarjeta 13: Implementación de Animaciones Funcionales**

* **Título:** Implementación de Animaciones Funcionales  
* **Descripción:** Añadir animaciones sutiles para mejorar la experiencia de usuario. Esto incluye transiciones entre pantallas y retroalimentación visual en botones o al agregar un producto al carrito.  
* **Categoría:** Animaciones UI/UX  
* **Checklist:**  
  * \[ \] Añadir animaciones de transición entre pantallas con Navigation-Compose.  
  * \[ \] Implementar animación de "añadir al carrito".  
  * \[ \] Añadir ripple effect a los botones principales.  
* **Miembro asignado:** David Larenas

### **Tarjeta 14: Documentación del Proyecto (README.md)**

* **Título:** Documentación del Proyecto (README.md)  
* **Descripción:** Redactar el archivo README.md del repositorio de GitHub. Debe incluir la descripción del proyecto, los nombres de los integrantes, las funcionalidades implementadas y los pasos para ejecutar la aplicación.  
* **Categoría:** Documentación  
* **Checklist:**  
  * \[ \] Redactar descripción del proyecto.  
  * \[ \] Listar a los integrantes del equipo.  
  * \[ \] Detallar las funcionalidades principales.  
  * \[ \] Explicar cómo clonar y ejecutar el proyecto.  
* **Miembro asignado:** Víctor Mena