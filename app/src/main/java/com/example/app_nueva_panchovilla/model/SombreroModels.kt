package com.example.app_nueva_panchovilla.model

import com.example.app_nueva_panchovilla.R
import java.util.Locale

enum class Etiqueta(val emoji: String, val texto: String) {
    OFERTA("🏷️", "OFERTA"),
    NUEVO("✨", "NUEVO"),
    POPULAR("🔥", "POPULAR"),
    EXCLUSIVO("👑", "EXCLUSIVO")
}

enum class Orden(val texto: String) {
    NINGUNO("Sin orden"),
    PRECIO_MENOR("Precio ↑"),
    PRECIO_MAYOR("Precio ↓"),
    CALIFICACION("Mejor calificados")
}

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

data class ItemCarrito(
    val producto: Sombrero,
    val cantidad: Int
)

data class Factura(
    val numero: Int,
    val fecha: String,
    val cliente: String,
    val celular: String,
    val items: List<ItemCarrito>,
    val total: Double
)

data class DestinoMenu(
    val ruta: String,
    val emoji: String,
    val nombre: String
)

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

fun formatoPrecio(valor: Double): String =
    "\$" + String.format(Locale.forLanguageTag("es-CO"), "%,.0f", valor)

fun textoFactura(f: Factura): String {
    val numero = String.format(Locale.getDefault(), "%03d", f.numero)
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