# 🤠 Pancho Villa – Aplicación móvil para una tienda de sombreros

Proyecto práctico de la asignatura **Aplicativos Móviles**, desarrollado en **Kotlin** con **Jetpack Compose** y **Material Design 3**.

---

## 1. Descripción del negocio

Pancho Villa es una tienda de sombreros [ajustar: tradicionales colombianos, artesanales, ciudad y quién la atiende]. Su catálogo incluye piezas como el sombrero aguadeño, elaborado completamente a mano, y el sombrero costeño, de estilo más fresco y diferente. Los productos se agrupan en categorías (artesanales, elegantes, llaneros, playa y fiesta) para facilitar su búsqueda.

La aplicación lleva ese catálogo al celular. A través de ella, el cliente puede ver los sombreros disponibles, decidir cuántas unidades desea, registrar sus datos y conservar la factura de cada compra. El negocio se apoya en tres elementos: un catálogo con tarjetas descriptivas (foto, descripción, calificación, precio y etiquetas como Oferta, Nuevo, Popular o Exclusivo), un proceso de pedido con factura y un historial de las compras realizadas.

## 2. Objetivo de la aplicación

El objetivo general es ofrecer a los clientes de Pancho Villa una herramienta móvil sencilla con la que puedan conocer los sombreros de la tienda, encontrarlos con facilidad y completar su compra con una factura clara.

Para lograrlo, se plantearon los siguientes objetivos específicos:

- Facilitar la búsqueda de productos mediante un buscador en tiempo real, un filtro por categorías y la opción de ordenar por precio o calificación.
- Simplificar la compra permitiendo elegir la cantidad de cada sombrero con botones de más y menos, con subtotales y total calculados de forma automática.
- Formalizar el pedido validando los datos del cliente y generando una factura con el resumen de la compra.
- Mantener un historial en el que se puedan consultar todas las facturas generadas.
- Ofrecer una interfaz moderna y coherente, con identidad visual propia, construida con Jetpack Compose y Material Design 3.

## 3. Identidad visual

La apariencia de la aplicación se configuró en los archivos `Color.kt`, `Theme.kt` y `Type.kt`, dentro de la carpeta `ui/theme`. Se definió una paleta propia en Material 3 inspirada en los materiales de los sombreros tradicionales: el cuero, la mostaza y el ladrillo, sobre un fondo crema.

| Color | Uso en la aplicación | Código |
|---|---|---|
| Cuero | Color primario y barra superior | `#7B4A1E` |
| Mostaza | Color secundario y estrellas de calificación | `#8C5A00` |
| Ladrillo | Color terciario y etiqueta de oferta | `#A6352B` |
| Crema | Fondo general | `#FFF8F2` |
| Tinta | Texto principal | `#1F1B16` |

En la tipografía se usó letra serif para los títulos, con el fin de dar un aire de tienda tradicional, y la fuente por defecto para los textos de lectura. Además, se definieron esquemas de color para tema claro y oscuro, y se evitó el uso de colores dinámicos para que la identidad de la marca se mantenga en cualquier dispositivo. Todos los colores de la interfaz, incluidos los de las etiquetas, las estrellas y la barra superior, se toman de `MaterialTheme.colorScheme`.

## 4. Pantallas implementadas

La aplicación se organiza con un `Scaffold` que contiene una barra superior (`TopAppBar`) y una barra de navegación inferior (`NavigationBar`). El cambio entre pantallas lo gestionan un `NavController` y un `NavHost` de Navigation Compose.

Cuatro pantallas son accesibles desde la barra inferior (Inicio, Trabajo, Carrito y Pedidos). Otras dos, Cantidad y Factura, se abren como parte del proceso de compra.

### 4.1. Inicio (`CatalogoScreen`)

Es la pantalla principal y funciona como catálogo de la tienda. En la parte superior se encuentra el campo de búsqueda (`OutlinedTextField`), que filtra mientras se escribe y cuenta con un botón para borrar el texto. Debajo se ubican las categorías, presentadas como chips seleccionables en una fila horizontal (`LazyRow` con `FilterChip`), y las opciones de orden: precio de menor a mayor, precio de mayor a menor y mejor calificados.

La lista de sombreros se construye con un `LazyColumn` a partir de la colección de datos, y cada elemento se dibuja con el componente reutilizable `ProductCard`. Cuando el carrito ya tiene productos, aparece además un botón de acceso rápido para finalizar la compra.

El filtrado y el orden se resuelven encadenando las condiciones sobre la lista original:

```kotlin
val productosFiltrados = listaSombreros
    .filter { categoriaSeleccionada == "Todos" || it.categoria == categoriaSeleccionada }
    .filter {
        it.name.contains(busqueda, ignoreCase = true) ||
                it.description.contains(busqueda, ignoreCase = true)
    }
    .let { lista ->
        when (orden) {
            Orden.PRECIO_MENOR -> lista.sortedBy { it.precio }
            Orden.PRECIO_MAYOR -> lista.sortedByDescending { it.precio }
            Orden.CALIFICACION -> lista.sortedByDescending { it.calificacion }
            Orden.NINGUNO -> lista
        }
    }
```

### 4.2. Cantidad (`CantidadScreen`)

Esta pantalla se abre al pulsar el botón Comprar de un sombrero. Muestra el producto y permite definir cuántas unidades se desean mediante los botones de menos y más, mientras el subtotal se actualiza en el momento. Al terminar, el cliente puede elegir entre agregar el producto y seguir explorando el catálogo, o agregarlo e ir directamente a finalizar la compra.

El contador se maneja con un estado local:

```kotlin
var cantidad by remember { mutableIntStateOf(1) }

OutlinedButton(onClick = { if (cantidad > 1) cantidad-- }) {
    Text("−")
}

Text(text = "$cantidad", style = MaterialTheme.typography.headlineMedium)

Button(onClick = { cantidad++ }) {
    Text("+")
}
```

### 4.3. Carrito (`CheckoutScreen`)

Presenta el resumen del pedido. Cada producto aparece en una tarjeta con su subtotal y con botones para modificar la cantidad; si esta llega a cero, el producto se retira del carrito. En la parte inferior se calcula el total, con formato en pesos colombianos, y se ubica el formulario con el nombre y el celular del cliente.

El nombre no puede estar vacío y el celular debe tener exactamente diez dígitos. Si el celular está incompleto, el campo se marca en error y muestra cuántos dígitos lleva. El botón Finalizar compra permanece desactivado mientras el carrito esté vacío o los datos no sean válidos:

```kotlin
val celularValido = celular.length == 10
val datosValidos = carrito.isNotEmpty() && nombre.isNotBlank() && celularValido

Button(
    onClick = onFinalizar,
    enabled = datosValidos,
    modifier = Modifier.fillMaxWidth()
) {
    Text("Finalizar compra")
}
```

### 4.4. Factura (`FacturaScreen`)

Se muestra automáticamente al finalizar la compra. La factura (`FacturaCard`) incluye el número consecutivo, la fecha y hora, los datos del cliente, el detalle de los productos y el total. Desde esta pantalla es posible compartirla como texto o continuar hacia el historial de pedidos o hacia el inicio.

### 4.5. Pedidos (`PedidosScreen`)

Funciona como historial. Cada compra queda registrada en una tarjeta independiente, de la más reciente a la más antigua. Si aún no se han realizado compras, la pantalla lo indica con un mensaje.

### 4.6. Trabajo (`TrabajoScreen`)

Es una sección reservada para versiones posteriores de la aplicación. Por ahora muestra un aviso indicando que se encuentra en construcción.

## 5. Navegación con Navigation Compose

Las rutas se centralizaron en un objeto llamado `Rutas`, y el `NavHost` declara cada pantalla mediante `composable`. La pantalla de cantidad recibe como argumento el identificador del sombrero seleccionado, y la de factura recibe el número de la factura creada.

```kotlin
object Rutas {
    const val INICIO = "inicio"
    const val TRABAJO = "trabajo"
    const val CARRITO = "carrito"
    const val PEDIDOS = "pedidos"
    const val CANTIDAD = "cantidad/{id}"
    const val FACTURA = "factura/{numero}"
}

NavHost(
    navController = navController,
    startDestination = Rutas.INICIO,
    modifier = Modifier.padding(innerPadding)
) {
    composable(Rutas.INICIO) { /* CatalogoScreen(...) */ }

    composable(
        route = Rutas.CANTIDAD,
        arguments = listOf(navArgument("id") { type = NavType.IntType })
    ) { entrada -> /* CantidadScreen(...) */ }

    composable(Rutas.CARRITO) { /* CheckoutScreen(...) */ }
    composable(Rutas.PEDIDOS) { /* PedidosScreen(...) */ }
}
```

## 6. Funcionalidades de la aplicación

La aplicación cuenta con tres funcionalidades principales: una basada en lo trabajado en clase y dos nuevas, pensadas para las necesidades de una tienda de sombreros.

### 6.1. Carrito de compras con cantidades (trabajada en clase)

Esta funcionalidad parte de lo visto en clase sobre agregar productos a un carrito y aumentar o disminuir sus cantidades. El cliente elige un sombrero, define cuántas unidades quiere y lo agrega al pedido; después puede seguir eligiendo o ir al carrito, donde todavía puede ajustar las cantidades. El subtotal cambia mientras se modifica el contador y el total del pedido se recalcula automáticamente en pesos colombianos. Si un producto se agrega más de una vez, las cantidades se suman en lugar de duplicar el elemento, y la barra inferior muestra una insignia con el número de productos que hay en el carrito.

### 6.2. Ordenar productos por precio o calificación (primera funcionalidad nueva)

La primera funcionalidad nueva permite al cliente reorganizar el catálogo según su interés: ver primero los sombreros más económicos, los más costosos o los mejor calificados. Las opciones disponibles son Precio ↑, Precio ↓, Mejor calificados y Sin orden, y la lista se reorganiza de inmediato al seleccionar cada una. Esta funcionalidad trabaja en conjunto con el buscador y el filtro por categorías, que complementan la experiencia del catálogo.

### 6.3. Formulario con validaciones y factura (segunda funcionalidad nueva)

La segunda funcionalidad nueva se activa al cerrar el pedido. La aplicación solicita los datos del cliente, los valida y genera una factura con el resumen de la compra. La validación exige un nombre no vacío y un celular con diez dígitos numéricos; mientras no se cumpla, el botón de finalizar permanece desactivado y el campo del celular muestra el error. Una vez generada, la factura incluye número consecutivo, fecha y hora, datos del cliente, detalle de productos y total, puede compartirse como texto y queda guardada de forma independiente en la pantalla de Pedidos.

> Nota: los datos se manejan de forma local y en memoria, de acuerdo con el alcance de este primer corte. Al cerrar la aplicación, el carrito y las facturas se reinician.

## 7. Estado, eventos y Navigation Compose

El estado de la aplicación se concentra en la función principal, `PanchoVillaApp`. Allí se usa `remember` con `mutableStateOf` para valores como la búsqueda, la categoría seleccionada, el orden y los datos del cliente, y `mutableStateListOf` para el carrito y las facturas. Se optó por ubicarlo allí para que la información se conserve al cambiar de pantalla. Cuando alguno de estos valores cambia, Compose vuelve a dibujar únicamente los componentes que dependen de él.

Los eventos del usuario se comunican mediante funciones lambda, como `onClick`, `onValueChange`, `onComprar` o `onFinalizar`. Los componentes se limitan a avisar lo que ocurrió, y la función que posee el estado decide cómo actualizarlo.

La navegación se construye con `rememberNavController`, que crea el controlador, y con el `NavHost`, que declara las rutas. El cambio de pantalla se realiza con `navigate`.

## 8. Instrucciones para ejecutar el proyecto

### Requisitos previos

- Android Studio en una versión reciente, con soporte para Jetpack Compose.
- JDK 17 o superior, que viene incluido por defecto en Android Studio.
- SDK de Android con la versión configurada en `app/build.gradle.kts` (Compile SDK: [completar], Minimum SDK: [completar]).
- Un emulador de Android (AVD) o un celular Android con la depuración por USB activada.

### Pasos

1. Obtener el proyecto clonando el repositorio, o descargando el archivo ZIP desde GitHub y descomprimiéndolo:

   ```bash
   git clone https://github.com/[usuario]/[repositorio].git
   ```

2. Abrir Android Studio, seleccionar **Open** y elegir la carpeta del proyecto.
3. Esperar a que termine la sincronización de Gradle, o pulsar **Sync Now** si el programa lo solicita. La dependencia de Navigation Compose ya está declarada en `app/build.gradle.kts`:

   ```kotlin
   implementation("androidx.navigation:navigation-compose:2.9.0")
   ```

4. Seleccionar un emulador o dispositivo y pulsar **Run**. La aplicación abre en la pantalla de Inicio.

## 9. Autor

Giancarlo Burbano Urbano 
Asignatura: Aplicativos Móviles
