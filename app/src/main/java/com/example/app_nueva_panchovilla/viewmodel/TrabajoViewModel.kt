package com.example.app_nueva_panchovilla.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_nueva_panchovilla.data.repository.TrabajoRepository
import com.example.app_nueva_panchovilla.model.FiltroJornada
import com.example.app_nueva_panchovilla.model.Jornada
import com.example.app_nueva_panchovilla.model.Trabajador
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TrabajoViewModel(
    private val repository: TrabajoRepository
) : ViewModel() {

    val trabajadores: StateFlow<List<Trabajador>> = repository.trabajadores
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val todasLasJornadas: StateFlow<List<Jornada>> = repository.jornadas
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _filtroActual = MutableStateFlow(FiltroJornada.TODAS)
    val filtroActual: StateFlow<FiltroJornada> = _filtroActual.asStateFlow()

    private val _trabajadorFiltroId = MutableStateFlow<Int?>(null)
    val trabajadorFiltroId: StateFlow<Int?> = _trabajadorFiltroId.asStateFlow()

    val jornadasFiltradas: StateFlow<List<Jornada>> = combine(
        repository.jornadas,
        _filtroActual,
        _trabajadorFiltroId
    ) { lista, filtro, trabajadorId ->
        lista
            .filter { jornada ->
                trabajadorId == null || jornada.trabajadorId == trabajadorId
            }
            .filter { jornada ->
                when (filtro) {
                    FiltroJornada.TODAS -> true
                    FiltroJornada.HORAS_EXTRA -> jornada.horasExtra > 0
                    FiltroJornada.DEBEN_HORAS -> jornada.horasDebidas > 0
                    FiltroJornada.PENDIENTES -> !jornada.pagada
                }
            }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun cambiarFiltro(filtro: FiltroJornada) {
        _filtroActual.value = filtro
    }

    fun filtrarPorTrabajador(trabajadorId: Int?) {
        _trabajadorFiltroId.value = trabajadorId
    }

    // --- ACCIONES TRABAJADORES ---

    fun agregarTrabajador(nombre: String, cargo: String, pagoPorDia: Double) {
        if (nombre.isBlank() || cargo.isBlank() || pagoPorDia <= 0) return
        viewModelScope.launch {
            repository.agregarTrabajador(nombre, cargo, pagoPorDia)
        }
    }

    fun actualizarTrabajador(trabajador: Trabajador) {
        if (trabajador.nombre.isBlank() || trabajador.cargo.isBlank() || trabajador.pagoPorDia <= 0) return
        viewModelScope.launch {
            repository.actualizarTrabajador(trabajador)
        }
    }

    fun eliminarTrabajador(trabajador: Trabajador) {
        viewModelScope.launch {
            repository.eliminarTrabajador(trabajador)
            if (_trabajadorFiltroId.value == trabajador.id) {
                _trabajadorFiltroId.value = null
            }
        }
    }

    // --- ACCIONES JORNADAS ---

    fun registrarJornada(
        trabajadorId: Int,
        fecha: String,
        horaEntrada: Int,
        horaSalida: Int
    ) {
        if (trabajadorId <= 0 || fecha.isBlank() || horaSalida <= horaEntrada) return
        viewModelScope.launch {
            repository.registrarJornada(trabajadorId, fecha, horaEntrada, horaSalida)
        }
    }

    fun actualizarJornada(
        id: Int,
        trabajadorId: Int,
        fecha: String,
        horaEntrada: Int,
        horaSalida: Int,
        pagada: Boolean
    ) {
        if (trabajadorId <= 0 || fecha.isBlank() || horaSalida <= horaEntrada) return
        viewModelScope.launch {
            repository.actualizarJornada(id, trabajadorId, fecha, horaEntrada, horaSalida, pagada)
        }
    }

    fun cambiarEstadoPago(jornada: Jornada) {
        viewModelScope.launch {
            repository.cambiarEstadoPago(jornada)
        }
    }

    fun eliminarJornada(jornada: Jornada) {
        viewModelScope.launch {
            repository.eliminarJornada(jornada)
        }
    }
}