package com.example.app_nueva_panchovilla.ui.screens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.dp
import com.example.app_nueva_panchovilla.model.Factura
import com.example.app_nueva_panchovilla.model.ItemCarrito
import com.example.app_nueva_panchovilla.model.Orden
import com.example.app_nueva_panchovilla.model.Sombrero
import com.example.app_nueva_panchovilla.model.categorias
import com.example.app_nueva_panchovilla.model.formatoPrecio
import com.example.app_nueva_panchovilla.model.textoFactura
import com.example.app_nueva_panchovilla.ui.components.CategoryBadge
import com.example.app_nueva_panchovilla.ui.components.FacturaCard
import com.example.app_nueva_panchovilla.ui.components.ProductCard

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

        item {
            Column {
                Text(
                    text = "Ordenar por",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(Orden.entries) { opcion ->
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
                Text(text = producto.name, style = MaterialTheme.typography.titleLarge)
                Text(text = producto.description, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${formatoPrecio(producto.precio)} c/u",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Cantidad", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(onClick = { if (cantidad > 1) cantidad-- }) { Text("−") }
            Text(
                text = "$cantidad",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Button(onClick = { cantidad++ }) { Text("+") }
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
            Text(text = "Tu pedido", style = MaterialTheme.typography.titleLarge)
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
                    Text(text = item.producto.name, style = MaterialTheme.typography.titleMedium)
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
                            OutlinedButton(onClick = { onCambiarCantidad(item.producto, -1) }) { Text("−") }
                            Text(
                                text = "${item.cantidad}",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            Button(onClick = { onCambiarCantidad(item.producto, 1) }) { Text("+") }
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
                Text(text = "Total", style = MaterialTheme.typography.titleLarge)
                Text(
                    text = formatoPrecio(total),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            Text(text = "Datos para la factura", style = MaterialTheme.typography.titleMedium)
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

        OutlinedButton(onClick = onVerPedidos, modifier = Modifier.fillMaxWidth()) {
            Text("Ver mis pedidos")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(onClick = onVolverInicio, modifier = Modifier.fillMaxWidth()) {
            Text("Volver al inicio")
        }
    }
}

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
            Text(text = "Mis pedidos (${facturas.size})", style = MaterialTheme.typography.titleLarge)
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