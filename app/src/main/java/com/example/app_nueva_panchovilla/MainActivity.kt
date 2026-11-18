package com.example.app_nueva_panchovilla

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.app_nueva_panchovilla.data.local.PanchoVillaDatabase
import com.example.app_nueva_panchovilla.data.repository.TrabajoRepository
import com.example.app_nueva_panchovilla.data.repository.TrabajoViewModelFactory
import com.example.app_nueva_panchovilla.model.Factura
import com.example.app_nueva_panchovilla.model.ItemCarrito
import com.example.app_nueva_panchovilla.model.Orden
import com.example.app_nueva_panchovilla.model.Rutas
import com.example.app_nueva_panchovilla.model.Sombrero
import com.example.app_nueva_panchovilla.model.destinosMenu
import com.example.app_nueva_panchovilla.model.listaSombreros
import com.example.app_nueva_panchovilla.ui.screens.CantidadScreen
import com.example.app_nueva_panchovilla.ui.screens.CatalogoScreen
import com.example.app_nueva_panchovilla.ui.screens.CheckoutScreen
import com.example.app_nueva_panchovilla.ui.screens.FacturaScreen
import com.example.app_nueva_panchovilla.ui.screens.PedidosScreen
import com.example.app_nueva_panchovilla.ui.screens.TrabajoScreen
import com.example.app_nueva_panchovilla.ui.theme.AppNuevaPanchoVillaTheme
import com.example.app_nueva_panchovilla.viewmodel.TrabajoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = PanchoVillaDatabase.getDatabase(applicationContext)
        val repository = TrabajoRepository(database.trabajoDao())
        val factory = TrabajoViewModelFactory(repository)

        setContent {
            AppNuevaPanchoVillaTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val trabajoViewModel: TrabajoViewModel = viewModel(factory = factory)
                    PanchoVillaApp(trabajoViewModel = trabajoViewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanchoVillaApp(trabajoViewModel: TrabajoViewModel) {
    val navController = rememberNavController()
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route

    var busqueda by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf("Todos") }
    var orden by remember { mutableStateOf(Orden.NINGUNO) }
    var nombreCliente by remember { mutableStateOf("") }
    var celularCliente by remember { mutableStateOf("") }
    val carrito = remember { mutableStateListOf<ItemCarrito>() }
    val facturas = remember { mutableStateListOf<Factura>() }

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

    fun irA(ruta: String) {
        navController.navigate(ruta) {
            popUpTo(Rutas.INICIO)
            launchSingleTop = true
        }
    }

    fun agregarAlCarrito(producto: Sombrero, cantidad: Int) {
        val indice = carrito.indexOfFirst { it.producto.id == producto.id }
        if (indice >= 0) {
            val actual = carrito[indice]
            carrito[indice] = actual.copy(cantidad = actual.cantidad + cantidad)
        } else {
            carrito.add(ItemCarrito(producto, cantidad))
        }
    }

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
                    modifier = Modifier.fillMaxSize(),
                    viewModel = trabajoViewModel
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