package com.example.app_nueva_panchovilla

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.app_nueva_panchovilla.ui.theme.AppNuevaPanchoVillaTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNuevaPanchoVillaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    PanchoVillaApp()
                }
            }
        }
    }
}

// ---------------------------------------------------------------------
// MODELOS Y DATOS
// ---------------------------------------------------------------------

// Tipos de etiqueta que se pueden poner sobre una tarjeta
enum class Etiqueta(val emoji: String, val texto: String) {
    OFERTA("🏷️", "OFERTA"),
    NUEVO("✨", "NUEVO"),
    POPULAR("🔥", "POPULAR"),
    EXCLUSIVO("👑", "EXCLUSIVO")
}

// Formas de ordenar el catálogo
enum class Orden(val texto: String) {
    NINGUNO("Sin orden"),
    PRECIO_MENOR("Precio ↑"),
    PRECIO_MAYOR("Precio ↓"),
    CALIFICACION("Mejor calificados")
}

// Para poder crear las variables de tipo sombrero
data class Sombrero(
    val id: Int,
    val name: String,
    val precio: Double,
    val imagen: Int,
    val description: String,
    val calificacion: Int,
    val categoria: String,
    val etiqueta: Etiqueta? = null
)

data class Category(
    val emoji: String,
    val name: String
)

// Un producto con su cantidad dentro del carrito
data class ItemCarrito(
    val producto: Sombrero,
    val cantidad: Int
)

// Una factura ya finalizada
data class Factura(
    val numero: Int,
    val fecha: String,
    val cliente: String,
    val celular: String,
    val items: List<ItemCarrito>,
    val total: Double
)

// Opción de la barra de navegación inferior
data class DestinoMenu(
    val ruta: String,
    val emoji: String,
    val nombre: String
)

// Rutas de Navigation Compose
object Rutas {
    const val INICIO = "inicio"
    const val TRABAJO = "trabajo"
    const val CARRITO = "carrito"
    const val PEDIDOS = "pedidos"
    const val CANTIDAD = "cantidad/{id}"
    const val FACTURA = "factura/{numero}"

    fun cantidad(id: Int) = "cantidad/$id"
    fun factura(numero: Int) = "factura/$numero"
}

val destinosMenu = listOf(
    DestinoMenu(Rutas.INICIO, "\uD83C\uDFE0", "Inicio"),
    DestinoMenu(Rutas.TRABAJO, "\uD83D\uDCBB", "Trabajo"),
    DestinoMenu(Rutas.CARRITO, "\uD83D\uDED2", "Carrito"),
    DestinoMenu(Rutas.PEDIDOS, "\uD83D\uDCCB", "Pedidos")
)

// Nota: los productos reutilizan las imágenes aguadeno y costeno.
// Cuando tengas más fotos, cópialas a res/drawable y cambia R.drawable.xxx
val listaSombreros = listOf(
    Sombrero(1, "Sombrero Aguadeño", 99000.0, R.drawable.aguadeno, "Realizado 100% a mano", 5, "Artesanales", Etiqueta.POPULAR),
    Sombrero(2, "Sombrero Costeño", 35000.0, R.drawable.costeno, "Un estilo fresco y diferente", 4, "Playa", Etiqueta.NUEVO),
    Sombrero(3, "Sombrero Ecuatoriano", 270000.0, R.drawable.ecua, "Ideal para el sol del llano", 4, "Llaneros"),
    Sombrero(4, "Sombrero Brisa", 70000.0, R.drawable.brisa, "Elegante, fino y muy liviano", 5, "Elegantes", Etiqueta.EXCLUSIVO),
    Sombrero(5, "Sombrero Charro", 40000.0, R.drawable.charro, "Bordado a mano con detalles en hilo", 3, "Fiesta", Etiqueta.OFERTA)
)

val categorias = listOf(
    Category("🤠", "Todos"),
    Category("🧵", "Artesanales"),
    Category("👑", "Elegantes"),
    Category("🐴", "Llaneros"),
    Category("🏖️", "Playa"),
    Category("🎉", "Fiesta")
)

// ---------------------------------------------------------------------
// FUNCIONES AUXILIARES
// ---------------------------------------------------------------------

// 99000.0 -> $99.000
fun formatoPrecio(valor: Double): String =
    "\$" + String.format(Locale.forLanguageTag("es-CO"), "%,.0f", valor)

// Texto de la factura para compartirla
fun textoFactura(f: Factura): String {
    val numero = String.format("%03d", f.numero)
    return buildString {
        appendLine("🤠 PANCHO VILLA 🤠")
        appendLine("Factura N° $numero")
        appendLine("Fecha: ${f.fecha}")
        appendLine("Cliente: ${f.cliente}")
        appendLine("Celular: ${f.celular}")
        appendLine("----------------------")
        for (item in f.items) {
            appendLine(
                "${item.cantidad} x ${item.producto.name}  " +
                        formatoPrecio(item.producto.precio * item.cantidad)
            )
        }
        appendLine("----------------------")
        appendLine("TOTAL: ${formatoPrecio(f.total)}")
        append("¡Gracias por tu compra!")
    }
}

// ---------------------------------------------------------------------
// APP PRINCIPAL (estado compartido + NavHost)
// ---------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanchoVillaApp() {
    val navController = rememberNavController()
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route

    // Estado de la app
    var busqueda by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf("Todos") }
    var orden by remember { mutableStateOf(Orden.NINGUNO) }
    var nombreCliente by remember { mutableStateOf("") }
    var celularCliente by remember { mutableStateOf("") }
    val carrito = remember { mutableStateListOf<ItemCarrito>() }
    val facturas = remember { mutableStateListOf<Factura>() }

    // Productos filtrados por categoría y búsqueda, y luego ordenados
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

    val totalItems = carrito.sumOf { it.cantidad }

    // Navegación desde la barra inferior
    fun irA(ruta: String) {
        navController.navigate(ruta) {
            popUpTo(Rutas.INICIO)
            launchSingleTop = true
        }
    }

    // Suma la cantidad si el producto ya está en el carrito
    fun agregarAlCarrito(producto: Sombrero, cantidad: Int) {
        val indice = carrito.indexOfFirst { it.producto.id == producto.id }
        if (indice >= 0) {
            val actual = carrito[indice]
            carrito[indice] = actual.copy(cantidad = actual.cantidad + cantidad)
        } else {
            carrito.add(ItemCarrito(producto, cantidad))
        }
    }

    // Sube o baja la cantidad; si llega a 0, quita el producto
    fun cambiarCantidad(producto: Sombrero, cambio: Int) {
        val indice = carrito.indexOfFirst { it.producto.id == producto.id }
        if (indice >= 0) {
            val nueva = carrito[indice].cantidad + cambio
            if (nueva <= 0) {
                carrito.removeAt(indice)
            } else {
                carrito[indice] = carrito[indice].copy(cantidad = nueva)
            }
        }
    }

    // Crea la factura, la guarda en pedidos, vacía el carrito y abre la factura
    fun finalizarCompra() {
        val factura = Factura(
            numero = facturas.size + 1,
            fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()),
            cliente = nombreCliente.trim(),
            celular = celularCliente,
            items = carrito.toList(),
            total = carrito.sumOf { it.producto.precio * it.cantidad }
        )
        facturas.add(0, factura)
        carrito.clear()
        nombreCliente = ""
        celularCliente = ""
        navController.navigate(Rutas.factura(factura.numero)) {
            popUpTo(Rutas.CARRITO) { inclusive = true }
        }
    }

    // Opción resaltada en la barra inferior
    val seccion = when (rutaActual) {
        Rutas.TRABAJO -> Rutas.TRABAJO
        Rutas.CARRITO -> Rutas.CARRITO
        Rutas.PEDIDOS, Rutas.FACTURA -> Rutas.PEDIDOS
        else -> Rutas.INICIO
    }

    val titulo = when (rutaActual) {
        Rutas.TRABAJO -> "💻 Trabajo"
        Rutas.CARRITO -> "🛒 Mi carrito"
        Rutas.PEDIDOS -> "📋 Mis pedidos"
        Rutas.CANTIDAD -> "Elegir cantidad"
        Rutas.FACTURA -> "Factura"
        else -> "🤠 Pancho Villa 🤠"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titulo) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar {
                destinosMenu.forEach { destino ->
                    NavigationBarItem(
                        selected = seccion == destino.ruta,
                        onClick = { irA(destino.ruta) },
                        icon = {
                            if (destino.ruta == Rutas.CARRITO && totalItems > 0) {
                                BadgedBox(badge = { Badge { Text("$totalItems") } }) {
                                    Text(destino.emoji)
                                }
                            } else {
                                Text(destino.emoji)
                            }
                        },
                        label = { Text(destino.nombre) }
                    )
                }
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Rutas.INICIO,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(Rutas.INICIO) {
                CatalogoScreen(
                    modifier = Modifier.fillMaxSize(),
                    productos = productosFiltrados,
                    categoriaSeleccionada = categoriaSeleccionada,
                    onCategoria = { categoriaSeleccionada = it },
                    orden = orden,
                    onOrden = { orden = it },
                    busqueda = busqueda,
                    onBusquedaChange = { busqueda = it },
                    itemsEnCarrito = totalItems,
                    onVerCarrito = { irA(Rutas.CARRITO) },
                    onComprar = { navController.navigate(Rutas.cantidad(it.id)) }
                )
            }

            composable(Rutas.TRABAJO) {
                TrabajoScreen(
                    modifier = Modifier.fillMaxSize()
                )
            }

            composable(
                route = Rutas.CANTIDAD,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { entrada ->
                val id = entrada.arguments?.getInt("id")
                val producto = listaSombreros.firstOrNull { it.id == id }
                if (producto != null) {
                    CantidadScreen(
                        modifier = Modifier.fillMaxSize(),
                        producto = producto,
                        onAgregarYSeguir = { cantidad ->
                            agregarAlCarrito(producto, cantidad)
                            navController.popBackStack()
                        },
                        onAgregarYFinalizar = { cantidad ->
                            agregarAlCarrito(producto, cantidad)
                            navController.navigate(Rutas.CARRITO) {
                                popUpTo(Rutas.CANTIDAD) { inclusive = true }
                            }
                        }
                    )
                }
            }

            composable(Rutas.CARRITO) {
                CheckoutScreen(
                    modifier = Modifier.fillMaxSize(),
                    carrito = carrito,
                    nombre = nombreCliente,
                    onNombreChange = { nombreCliente = it },
                    celular = celularCliente,
                    onCelularChange = { celularCliente = it.filter { c -> c.isDigit() } },
                    onCambiarCantidad = { producto, cambio -> cambiarCantidad(producto, cambio) },
                    onSeguirElegiendo = { irA(Rutas.INICIO) },
                    onFinalizar = { finalizarCompra() }
                )
            }

            composable(
                route = Rutas.FACTURA,
                arguments = listOf(navArgument("numero") { type = NavType.IntType })
            ) { entrada ->
                val numero = entrada.arguments?.getInt("numero")
                val factura = facturas.firstOrNull { it.numero == numero }
                if (factura != null) {
                    FacturaScreen(
                        modifier = Modifier.fillMaxSize(),
                        factura = factura,
                        onVerPedidos = { irA(Rutas.PEDIDOS) },
                        onVolverInicio = { irA(Rutas.INICIO) }
                    )
                }
            }

            composable(Rutas.PEDIDOS) {
                PedidosScreen(
                    modifier = Modifier.fillMaxSize(),
                    facturas = facturas
                )
            }
        }
    }
}

// ---------------------------------------------------------------------
// PANTALLAS
// ---------------------------------------------------------------------

// Inicio: búsqueda, categorías, orden y lista de productos
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    modifier: Modifier,
    productos: List<Sombrero>,
    categoriaSeleccionada: String,
    onCategoria: (String) -> Unit,
    orden: Orden,
    onOrden: (Orden) -> Unit,
    busqueda: String,
    onBusquedaChange: (String) -> Unit,
    itemsEnCarrito: Int,
    onVerCarrito: () -> Unit,
    onComprar: (Sombrero) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            OutlinedTextField(
                value = busqueda,
                onValueChange = onBusquedaChange,
                label = { Text("Buscar productos") },
                singleLine = true,
                trailingIcon = {
                    if (busqueda.isNotEmpty()) {
                        TextButton(onClick = { onBusquedaChange("") }) {
                            Text("✕")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Acceso rápido al carrito cuando ya hay productos
        if (itemsEnCarrito > 0) {
            item {
                Button(
                    onClick = onVerCarrito,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🛒 Ver carrito ($itemsEnCarrito) · Finalizar compra")
                }
            }
        }

        // Categorías seleccionables
        item {
            Column {
                Text(
                    text = "Categorías",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categorias) { category ->
                        CategoryBadge(
                            category = category,
                            seleccionada = category.name == categoriaSeleccionada,
                            onClick = { onCategoria(category.name) }
                        )
                    }
                }
            }
        }

        // Ordenar
        item {
            Column {
                Text(
                    text = "Ordenar por",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(Orden.values().toList()) { opcion ->
                        FilterChip(
                            selected = opcion == orden,
                            onClick = { onOrden(opcion) },
                            label = { Text(opcion.texto) }
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Sombreros Disponibles (${productos.size})",
                style = MaterialTheme.typography.titleLarge
            )
        }

        // Productos (ya filtrados y ordenados)
        if (productos.isEmpty()) {
            item {
                Text(
                    text = "No se encontraron productos con esos filtros.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            items(productos, key = { it.id }) { sombrero ->
                ProductCard(
                    product = sombrero,
                    onComprar = { onComprar(sombrero) }
                )
            }
        }
    }
}

// Trabajo: pantalla pendiente de definir
@Composable
fun TrabajoScreen(modifier: Modifier) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "💻",
            fontSize = 48.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Trabajo",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "Esta sección está en construcción.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

// Pantalla para elegir la cantidad con - y +
@Composable
fun CantidadScreen(
    modifier: Modifier,
    producto: Sombrero,
    onAgregarYSeguir: (Int) -> Unit,
    onAgregarYFinalizar: (Int) -> Unit
) {
    var cantidad by remember { mutableIntStateOf(1) }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Image(
                    painter = painterResource(id = producto.imagen),
                    contentDescription = producto.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = producto.name,
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = producto.description,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${formatoPrecio(producto.precio)} c/u",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Cantidad",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(onClick = { if (cantidad > 1) cantidad-- }) {
                Text("−")
            }

            Text(
                text = "$cantidad",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Button(onClick = { cantidad++ }) {
                Text("+")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Subtotal: ${formatoPrecio(producto.precio * cantidad)}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = { onAgregarYSeguir(cantidad) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar y seguir eligiendo")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { onAgregarYFinalizar(cantidad) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar y finalizar compra")
        }
    }
}

// Carrito + datos del cliente (formulario con validación)
@Composable
fun CheckoutScreen(
    modifier: Modifier,
    carrito: List<ItemCarrito>,
    nombre: String,
    onNombreChange: (String) -> Unit,
    celular: String,
    onCelularChange: (String) -> Unit,
    onCambiarCantidad: (Sombrero, Int) -> Unit,
    onSeguirElegiendo: () -> Unit,
    onFinalizar: () -> Unit
) {
    val total = carrito.sumOf { it.producto.precio * it.cantidad }
    val celularValido = celular.length == 10
    val datosValidos = carrito.isNotEmpty() && nombre.isNotBlank() && celularValido

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Tu pedido",
                style = MaterialTheme.typography.titleLarge
            )
        }

        if (carrito.isEmpty()) {
            item {
                Text(
                    text = "Tu carrito está vacío. Vuelve al inicio para elegir productos.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        items(carrito, key = { it.producto.id }) { item ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = item.producto.name,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "${formatoPrecio(item.producto.precio)} c/u",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedButton(onClick = { onCambiarCantidad(item.producto, -1) }) {
                                Text("−")
                            }

                            Text(
                                text = "${item.cantidad}",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )

                            Button(onClick = { onCambiarCantidad(item.producto, 1) }) {
                                Text("+")
                            }
                        }

                        Text(
                            text = formatoPrecio(item.producto.precio * item.cantidad),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = formatoPrecio(total),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            Text(
                text = "Datos para la factura",
                style = MaterialTheme.typography.titleMedium
            )
        }

        item {
            OutlinedTextField(
                value = nombre,
                onValueChange = onNombreChange,
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = celular,
                onValueChange = onCelularChange,
                label = { Text("Celular") },
                isError = celular.isNotEmpty() && !celularValido,
                supportingText = { Text("Debe tener 10 dígitos (${celular.length}/10)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Column {
                OutlinedButton(
                    onClick = onSeguirElegiendo,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Seguir eligiendo productos")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onFinalizar,
                    enabled = datosValidos,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Finalizar compra")
                }
            }
        }
    }
}

// Factura recién creada
@Composable
fun FacturaScreen(
    modifier: Modifier,
    factura: Factura,
    onVerPedidos: () -> Unit,
    onVolverInicio: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "✅ ¡Compra realizada!",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        FacturaCard(factura = factura)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, textoFactura(factura))
                }
                context.startActivity(Intent.createChooser(intent, "Compartir factura"))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Compartir factura")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onVerPedidos,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver mis pedidos")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onVolverInicio,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al inicio")
        }
    }
}

// Lista de facturas, una tarjeta por pedido
@Composable
fun PedidosScreen(
    modifier: Modifier,
    facturas: List<Factura>
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Mis pedidos (${facturas.size})",
                style = MaterialTheme.typography.titleLarge
            )
        }

        if (facturas.isEmpty()) {
            item {
                Text(
                    text = "Aún no tienes pedidos. Cuando finalices una compra, tu factura aparecerá aquí.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        items(facturas, key = { it.numero }) { factura ->
            FacturaCard(factura = factura)
        }
    }
}

// ---------------------------------------------------------------------
// COMPONENTES REUTILIZABLES
// ---------------------------------------------------------------------

// Factura con aspecto de recibo (sirve para tomarle captura)
@Composable
fun FacturaCard(factura: Factura) {
    val numero = String.format("%03d", factura.numero)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "🤠 PANCHO VILLA 🤠",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Factura N° $numero",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            Text("Fecha: ${factura.fecha}", style = MaterialTheme.typography.bodyMedium)
            Text("Cliente: ${factura.cliente}", style = MaterialTheme.typography.bodyMedium)
            Text("Celular: ${factura.celular}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            for (item in factura.items) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${item.cantidad} x ${item.producto.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = formatoPrecio(item.producto.precio * item.cantidad),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "TOTAL",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = formatoPrecio(factura.total),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "¡Gracias por tu compra!",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

// Chip de categoría seleccionable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryBadge(
    category: Category,
    seleccionada: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = seleccionada,
        onClick = onClick,
        label = { Text("${category.emoji} ${category.name}") }
    )
}

// Etiqueta de colores que flota sobre la tarjeta (Oferta, Nuevo, Popular...)
@Composable
fun EtiquetaBadge(etiqueta: Etiqueta, modifier: Modifier = Modifier) {
    val colores = MaterialTheme.colorScheme
    val (fondo, texto) = when (etiqueta) {
        Etiqueta.OFERTA -> colores.tertiary to colores.onTertiary
        Etiqueta.NUEVO -> colores.primary to colores.onPrimary
        Etiqueta.POPULAR -> colores.secondary to colores.onSecondary
        Etiqueta.EXCLUSIVO -> colores.inverseSurface to colores.inverseOnSurface
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = fondo)
    ) {
        Text(
            text = "${etiqueta.emoji} ${etiqueta.texto}",
            color = texto,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

// Muestra 5 estrellas: llenas según la calificación y vacías el resto
@Composable
fun RatingStars(calificacion: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        repeat(5) { indice ->
            val llena = indice < calificacion
            Text(
                text = if (llena) "★" else "☆",
                color = if (llena) MaterialTheme.colorScheme.secondary
                else MaterialTheme.colorScheme.outline,
                fontSize = 20.sp
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$calificacion/5",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

// La etiqueta se puede elegir al dibujar la tarjeta.
// Si no se pasa, usa la que trae el producto (o ninguna).
@Composable
fun ProductCard(
    product: Sombrero,
    onComprar: () -> Unit,
    etiqueta: Etiqueta? = product.etiqueta
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Image(
                    painter = painterResource(id = product.imagen),
                    contentDescription = product.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                RatingStars(calificacion = product.calificacion)

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Precio: ${formatoPrecio(product.precio)}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Button(onClick = onComprar) {
                        Text("Comprar")
                    }
                }
            }
        }

        // Etiqueta flotante: solo se dibuja si hay una elegida
        if (etiqueta != null) {
            EtiquetaBadge(
                etiqueta = etiqueta,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PanchoVillaAppPreview() {
    AppNuevaPanchoVillaTheme {
        PanchoVillaApp()
    }
}