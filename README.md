# 🤠 Sombreros Pancho Villa — App Android

Aplicación móvil nativa desarrollada en **Kotlin** y **Jetpack Compose** bajo arquitectura **MVVM** con persistencia local mediante **Room (SQLite)**, diseñada para la gestión comercial y el control operativo de turnos de la fábrica de sombreros **Pancho Villa**.

---

## 📋 ¿Qué se implementó?

* **Arquitectura MVVM:** Separación modular por capas (`model`, `data.local`, `data.repository`, `viewmodel`, `ui.components`, `ui.screens` y `ui.theme`), utilizando `StateFlow` y `ViewModelFactory` para la gestión reactiva del estado.
* **Base de datos local con Room (2 entidades relacionadas):** Persistencia estructurada en `PanchoVillaDatabase` mediante `TrabajadorEntity` y `JornadaEntity` vinculadas por clave foránea (`ForeignKey`), con consultas reactivas (`Flow`).
* **CRUD completo y lógica de negocio (Sección Trabajo):** Registro, consulta, edición y eliminación de trabajadores y jornadas laborales. Calcula automáticamente el pago y las horas trabajadas sobre el turno estándar de fábrica (**7:00 a. m. a 3:00 p. m. = 8 horas = 1 día**), identificando días completos, horas extra (`+h`), horas debidas (`-h`) y estado de pago.
* **Catálogo Comercial, Carrito y Facturación:** Búsqueda en tiempo real, filtros por categoría, ordenamiento por precio o calificación, carrito dinámico con validaciones de entrada y generación de factura compartible.

---

## 🧪 Pasos para acceder y probar cada funcionalidad

1. **Gestión de Trabajadores (CRUD + Persistencia en Room):**
   * Ingresar a la pestaña inferior **Trabajo**.
   * Registrar un nuevo trabajador ingresando nombre, cargo y pago por día.
   * Editar sus datos desde el ícono de lápiz, registrar un segundo trabajador y eliminar uno de ellos.
   * Cerrar completamente la aplicación y volver a abrirla para comprobar que los cambios se conservan en la base de datos local.

2. **Control de Turnos y Cálculo de Horas:**
   * En la misma pestaña **Trabajo**, registrar una jornada seleccionando un trabajador, fecha, hora de entrada y hora de salida (por ejemplo, de `7` a `17` para validar **1 día + 2 horas extra**, o de `7` a `13` para validar **2 horas debidas**).
   * Utilizar los filtros superiores (`Todas`, `Horas extra`, `Deben horas`, `Pendientes`), marcar la jornada como **Pagada**, editarla o eliminarla.
   * Reiniciar la aplicación para verificar que todos los registros y estados persisten.

3. **Catálogo, Carrito y Facturación:**
   * Ingresar a la pestaña **Catálogo**, filtrar sombreros por categoría o buscar por nombre y agregar productos al carrito.
   * Abrir la pestaña **Carrito**, ajustar cantidades, completar los datos del cliente (nombre y celular de 10 dígitos) y generar la factura del pedido.

---

## 🏗️ Estructura del proyecto

```text
com.example.app_nueva_panchovilla
├── data
│   ├── local        # Entidades Room, DAO y PanchoVillaDatabase
│   └── repository   # Repositorio de datos (TrabajoRepository)
├── model            # Modelos de dominio y enumeraciones de filtro
├── ui
│   ├── components   # Componentes reutilizables (tarjetas, diálogos)
│   ├── screens      # Pantallas principales (Catálogo, Carrito, Trabajo)
│   └── theme        # Paleta de colores y tipografía Material 3
└── viewmodel        # TrabajoViewModel y TrabajoViewModelFactory
```
