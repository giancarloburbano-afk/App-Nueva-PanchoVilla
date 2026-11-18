package com.example.app_nueva_panchovilla.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.app_nueva_panchovilla.model.FiltroJornada
import com.example.app_nueva_panchovilla.model.Jornada
import com.example.app_nueva_panchovilla.model.Trabajador
import com.example.app_nueva_panchovilla.ui.components.JornadaDialog
import com.example.app_nueva_panchovilla.ui.components.JornadaItemCard
import com.example.app_nueva_panchovilla.ui.components.ResumenTrabajoCard
import com.example.app_nueva_panchovilla.ui.components.TrabajadorDialog
import com.example.app_nueva_panchovilla.ui.components.TrabajadorItemCard
import com.example.app_nueva_panchovilla.viewmodel.TrabajoViewModel

@Composable
fun TrabajoScreen(
    modifier: Modifier,
    viewModel: TrabajoViewModel
) {
    val trabajadores by viewModel.trabajadores.collectAsState()
    val todasLasJornadas by viewModel.todasLasJornadas.collectAsState()
    val jornadasFiltradas by viewModel.jornadasFiltradas.collectAsState()
    val filtroActual by viewModel.filtroActual.collectAsState()
    val trabajadorFiltroId by viewModel.trabajadorFiltroId.collectAsState()

    var mostrarDialogoTrabajador by remember { mutableStateOf(false) }
    var trabajadorAEditar by remember { mutableStateOf<Trabajador?>(null) }

    var mostrarDialogoJornada by remember { mutableStateOf(false) }
    var jornadaAEditar by remember { mutableStateOf<Jornada?>(null) }

    val diasCompletos = todasLasJornadas.count { it.diaCompleto }
    val totalHorasExtra = todasLasJornadas.sumOf { it.horasExtra }
    val totalHorasDebidas = todasLasJornadas.sumOf { it.horasDebidas }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ResumenTrabajoCard(
                totalTrabajadores = trabajadores.size,
                diasCompletos = diasCompletos,
                totalHorasExtra = totalHorasExtra,
                totalHorasDebidas = totalHorasDebidas
            )
        }

        // Botones principales de registro
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        trabajadorAEditar = null
                        mostrarDialogoTrabajador = true
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("+ Trabajador")
                }

                Button(
                    onClick = {
                        jornadaAEditar = null
                        mostrarDialogoJornada = true
                    },
                    enabled = trabajadores.isNotEmpty(),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("+ Registrar turno")
                }
            }
        }

        // Sección de Trabajadores
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Trabajadores (${trabajadores.size})",
                        style = MaterialTheme.typography.titleLarge
                    )
                    if (trabajadorFiltroId != null) {
                        TextButton(onClick = { viewModel.filtrarPorTrabajador(null) }) {
                            Text("Ver todos")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (trabajadores.isEmpty()) {
                    Text(
                        text = "Agrega tu primer trabajador para empezar a registrar turnos de 7:00 a. m. a 3:00 p. m.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(trabajadores, key = { it.id }) { trabajador ->
                            val estaSeleccionado = trabajador.id == trabajadorFiltroId
                            TrabajadorItemCard(
                                trabajador = trabajador,
                                seleccionado = estaSeleccionado,
                                onSeleccionar = {
                                    viewModel.filtrarPorTrabajador(
                                        if (estaSeleccionado) null else trabajador.id
                                    )
                                },
                                onEditar = {
                                    trabajadorAEditar = trabajador
                                    mostrarDialogoTrabajador = true
                                },
                                onEliminar = {
                                    viewModel.eliminarTrabajador(trabajador)
                                }
                            )
                        }
                    }
                }
            }
        }

        // Filtros de Jornadas
        item {
            Column {
                Text(
                    text = "Historial de turnos (${jornadasFiltradas.size})",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(FiltroJornada.entries) { filtro ->
                        FilterChip(
                            selected = filtro == filtroActual,
                            onClick = { viewModel.cambiarFiltro(filtro) },
                            label = { Text(filtro.texto) }
                        )
                    }
                }
            }
        }

        if (jornadasFiltradas.isEmpty()) {
            item {
                Text(
                    text = "No hay turnos registrados para este filtro.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            items(jornadasFiltradas, key = { it.id }) { jornada ->
                JornadaItemCard(
                    jornada = jornada,
                    onCambiarPago = { viewModel.cambiarEstadoPago(jornada) },
                    onEditar = {
                        jornadaAEditar = jornada
                        mostrarDialogoJornada = true
                    },
                    onEliminar = { viewModel.eliminarJornada(jornada) }
                )
            }
        }
    }

    if (mostrarDialogoTrabajador) {
        TrabajadorDialog(
            trabajadorInicial = trabajadorAEditar,
            onDismiss = { mostrarDialogoTrabajador = false },
            onGuardar = { nombre, cargo, pagoPorDia ->
                val actual = trabajadorAEditar
                if (actual == null) {
                    viewModel.agregarTrabajador(nombre, cargo, pagoPorDia)
                } else {
                    viewModel.actualizarTrabajador(
                        actual.copy(nombre = nombre, cargo = cargo, pagoPorDia = pagoPorDia)
                    )
                }
                mostrarDialogoTrabajador = false
            }
        )
    }

    if (mostrarDialogoJornada && trabajadores.isNotEmpty()) {
        JornadaDialog(
            trabajadores = trabajadores,
            jornadaInicial = jornadaAEditar,
            onDismiss = { mostrarDialogoJornada = false },
            onGuardar = { trabajadorId, fecha, horaEntrada, horaSalida ->
                val actual = jornadaAEditar
                if (actual == null) {
                    viewModel.registrarJornada(trabajadorId, fecha, horaEntrada, horaSalida)
                } else {
                    viewModel.actualizarJornada(
                        id = actual.id,
                        trabajadorId = trabajadorId,
                        fecha = fecha,
                        horaEntrada = horaEntrada,
                        horaSalida = horaSalida,
                        pagada = actual.pagada
                    )
                }
                mostrarDialogoJornada = false
            }
        )
    }
}