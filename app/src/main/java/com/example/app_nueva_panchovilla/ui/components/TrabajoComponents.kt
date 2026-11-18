package com.example.app_nueva_panchovilla.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.app_nueva_panchovilla.model.Jornada
import com.example.app_nueva_panchovilla.model.Trabajador
import com.example.app_nueva_panchovilla.model.calcularTurno
import com.example.app_nueva_panchovilla.model.formatearHora
import com.example.app_nueva_panchovilla.model.formatoPrecio
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ResumenTrabajoCard(
    totalTrabajadores: Int,
    diasCompletos: Int,
    totalHorasExtra: Int,
    totalHorasDebidas: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "📊 Resumen Taller Pancho Villa",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "Turno base: 7:00 a. m. – 3:00 p. m. (8 horas = 1 día)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                EstadisticaItem("👷 Equipo", "$totalTrabajadores")
                EstadisticaItem("📅 Días ok", "$diasCompletos")
                EstadisticaItem("⏱️ Extra", "+${totalHorasExtra}h")
                EstadisticaItem("⚠️ Debidas", "-${totalHorasDebidas}h")
            }
        }
    }
}

@Composable
private fun EstadisticaItem(titulo: String, valor: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = valor,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun TrabajadorItemCard(
    trabajador: Trabajador,
    seleccionado: Boolean,
    onSeleccionar: () -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        onClick = onSeleccionar,
        modifier = Modifier.width(230.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (seleccionado) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainerLow
            }
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = trabajador.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = trabajador.cargo,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${formatoPrecio(trabajador.pagoPorDia)} / día",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEditar) {
                    Text("✏️ Editar")
                }
                TextButton(onClick = onEliminar) {
                    Text("🗑️", color = MaterialTheme.colorScheme.tertiary)
                }
            }
        }
    }
}

@Composable
fun JornadaItemCard(
    jornada: Jornada,
    onCambiarPago: () -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "👷 ${jornada.nombreTrabajador}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "📅 ${jornada.fecha} · ${formatearHora(jornada.horaEntrada)} a ${formatearHora(jornada.horaSalida)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = if (jornada.pagada) {
                        MaterialTheme.colorScheme.secondaryContainer
                    } else {
                        MaterialTheme.colorScheme.tertiaryContainer
                    }
                ) {
                    Text(
                        text = if (jornada.pagada) "✅ Pagada" else "⏳ Pendiente",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Total: ${jornada.horasTrabajadas}h",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                if (jornada.diaCompleto) {
                    Text(
                        text = "• Día completo",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                if (jornada.horasExtra > 0) {
                    Text(
                        text = "• +${jornada.horasExtra}h extra",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                if (jornada.horasDebidas > 0) {
                    Text(
                        text = "• Debe ${jornada.horasDebidas}h",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(onClick = onCambiarPago) {
                    Text(if (jornada.pagada) "Marcar pendiente" else "Marcar pagada")
                }

                Row {
                    TextButton(onClick = onEditar) {
                        Text("✏️ Editar")
                    }
                    TextButton(onClick = onEliminar) {
                        Text("🗑️", color = MaterialTheme.colorScheme.tertiary)
                    }
                }
            }
        }
    }
}

@Composable
fun TrabajadorDialog(
    trabajadorInicial: Trabajador?,
    onDismiss: () -> Unit,
    onGuardar: (nombre: String, cargo: String, pagoPorDia: Double) -> Unit
) {
    var nombre by remember { mutableStateOf(trabajadorInicial?.nombre ?: "") }
    var cargo by remember { mutableStateOf(trabajadorInicial?.cargo ?: "") }
    var pagoTexto by remember {
        mutableStateOf(
            if (trabajadorInicial != null) trabajadorInicial.pagoPorDia.toInt().toString() else "50000"
        )
    }

    val pagoValor = pagoTexto.toDoubleOrNull() ?: 0.0
    val esValido = nombre.isNotBlank() && cargo.isNotBlank() && pagoValor > 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (trabajadorInicial == null) "Nuevo trabajador" else "Editar trabajador")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre completo") },
                    singleLine = true,
                    isError = nombre.isBlank(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = cargo,
                    onValueChange = { cargo = it },
                    label = { Text("Cargo (ej. Tejedor, Hormador)") },
                    singleLine = true,
                    isError = cargo.isBlank(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = pagoTexto,
                    onValueChange = { pagoTexto = it.filter { c -> c.isDigit() } },
                    label = { Text("Pago por día (COP)") },
                    singleLine = true,
                    isError = pagoValor <= 0,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onGuardar(nombre, cargo, pagoValor) },
                enabled = esValido
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun JornadaDialog(
    trabajadores: List<Trabajador>,
    jornadaInicial: Jornada?,
    onDismiss: () -> Unit,
    onGuardar: (trabajadorId: Int, fecha: String, horaEntrada: Int, horaSalida: Int) -> Unit
) {
    val fechaHoy = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    }
    var trabajadorIdSeleccionado by remember {
        mutableIntStateOf(jornadaInicial?.trabajadorId ?: (trabajadores.firstOrNull()?.id ?: 0))
    }
    var fecha by remember { mutableStateOf(jornadaInicial?.fecha ?: fechaHoy) }
    var horaEntrada by remember { mutableIntStateOf(jornadaInicial?.horaEntrada ?: 7) }
    var horaSalida by remember { mutableIntStateOf(jornadaInicial?.horaSalida ?: 15) }

    val calculo = calcularTurno(horaEntrada, horaSalida)
    val esValido = trabajadorIdSeleccionado > 0 && fecha.isNotBlank() && horaSalida > horaEntrada

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (jornadaInicial == null) "Registrar turno" else "Editar turno")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Trabajador:", style = MaterialTheme.typography.labelLarge)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(trabajadores, key = { it.id }) { trabajador ->
                        FilterChip(
                            selected = trabajador.id == trabajadorIdSeleccionado,
                            onClick = { trabajadorIdSeleccionado = trabajador.id },
                            label = { Text(trabajador.nombre) }
                        )
                    }
                }

                OutlinedTextField(
                    value = fecha,
                    onValueChange = { fecha = it },
                    label = { Text("Fecha (dd/MM/aaaa)") },
                    singleLine = true,
                    isError = fecha.isBlank(),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Entrada: ${formatearHora(horaEntrada)}")
                    Row {
                        OutlinedButton(onClick = { if (horaEntrada > 0) horaEntrada-- }) { Text("−") }
                        Spacer(modifier = Modifier.width(6.dp))
                        Button(onClick = { if (horaEntrada < horaSalida - 1) horaEntrada++ }) { Text("+") }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Salida: ${formatearHora(horaSalida)}")
                    Row {
                        OutlinedButton(onClick = { if (horaSalida > horaEntrada + 1) horaSalida-- }) { Text("−") }
                        Spacer(modifier = Modifier.width(6.dp))
                        Button(onClick = { if (horaSalida < 24) horaSalida++ }) { Text("+") }
                    }
                }

                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Horas trabajadas: ${calculo.horasTrabajadas} h (${if (calculo.diaCompleto) "Día completo" else "Incompleto"})",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Horas extra: +${calculo.horasExtra} h · Horas debidas: ${calculo.horasDebidas} h",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onGuardar(trabajadorIdSeleccionado, fecha, horaEntrada, horaSalida) },
                enabled = esValido
            ) {
                Text("Guardar turno")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}