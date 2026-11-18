package com.example.app_nueva_panchovilla.data.repository

import com.example.app_nueva_panchovilla.data.local.JornadaEntity
import com.example.app_nueva_panchovilla.data.local.TrabajadorEntity
import com.example.app_nueva_panchovilla.data.local.TrabajoDao
import com.example.app_nueva_panchovilla.model.Jornada
import com.example.app_nueva_panchovilla.model.Trabajador
import com.example.app_nueva_panchovilla.model.calcularTurno
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class TrabajoRepository(private val dao: TrabajoDao) {

    val trabajadores: Flow<List<Trabajador>> = dao.obtenerTrabajadores().map { lista ->
        lista.map { entity ->
            Trabajador(
                id = entity.id,
                nombre = entity.nombre,
                cargo = entity.cargo,
                pagoPorDia = entity.pagoPorDia
            )
        }
    }

    val jornadas: Flow<List<Jornada>> = combine(
        dao.obtenerTrabajadores(),
        dao.obtenerJornadas()
    ) { listaTrabajadores, listaJornadas ->
        val mapaNombres = listaTrabajadores.associateBy({ it.id }, { it.nombre })
        listaJornadas.map { entity ->
            Jornada(
                id = entity.id,
                trabajadorId = entity.trabajadorId,
                nombreTrabajador = mapaNombres[entity.trabajadorId] ?: "Trabajador",
                fecha = entity.fecha,
                horaEntrada = entity.horaEntrada,
                horaSalida = entity.horaSalida,
                horasTrabajadas = entity.horasTrabajadas,
                diaCompleto = entity.diaCompleto,
                horasExtra = entity.horasExtra,
                horasDebidas = entity.horasDebidas,
                pagada = entity.pagada
            )
        }
    }

    suspend fun agregarTrabajador(nombre: String, cargo: String, pagoPorDia: Double) {
        dao.insertarTrabajador(
            TrabajadorEntity(
                nombre = nombre.trim(),
                cargo = cargo.trim(),
                pagoPorDia = pagoPorDia
            )
        )
    }

    suspend fun actualizarTrabajador(trabajador: Trabajador) {
        dao.actualizarTrabajador(
            TrabajadorEntity(
                id = trabajador.id,
                nombre = trabajador.nombre.trim(),
                cargo = trabajador.cargo.trim(),
                pagoPorDia = trabajador.pagoPorDia
            )
        )
    }

    suspend fun eliminarTrabajador(trabajador: Trabajador) {
        dao.eliminarTrabajador(
            TrabajadorEntity(
                id = trabajador.id,
                nombre = trabajador.nombre,
                cargo = trabajador.cargo,
                pagoPorDia = trabajador.pagoPorDia
            )
        )
    }

    suspend fun registrarJornada(
        trabajadorId: Int,
        fecha: String,
        horaEntrada: Int,
        horaSalida: Int
    ) {
        val calculo = calcularTurno(horaEntrada, horaSalida)
        dao.insertarJornada(
            JornadaEntity(
                trabajadorId = trabajadorId,
                fecha = fecha.trim(),
                horaEntrada = horaEntrada,
                horaSalida = horaSalida,
                horasTrabajadas = calculo.horasTrabajadas,
                diaCompleto = calculo.diaCompleto,
                horasExtra = calculo.horasExtra,
                horasDebidas = calculo.horasDebidas,
                pagada = false
            )
        )
    }

    suspend fun actualizarJornada(
        id: Int,
        trabajadorId: Int,
        fecha: String,
        horaEntrada: Int,
        horaSalida: Int,
        pagada: Boolean
    ) {
        val calculo = calcularTurno(horaEntrada, horaSalida)
        dao.actualizarJornada(
            JornadaEntity(
                id = id,
                trabajadorId = trabajadorId,
                fecha = fecha.trim(),
                horaEntrada = horaEntrada,
                horaSalida = horaSalida,
                horasTrabajadas = calculo.horasTrabajadas,
                diaCompleto = calculo.diaCompleto,
                horasExtra = calculo.horasExtra,
                horasDebidas = calculo.horasDebidas,
                pagada = pagada
            )
        )
    }

    suspend fun cambiarEstadoPago(jornada: Jornada) {
        dao.actualizarJornada(
            JornadaEntity(
                id = jornada.id,
                trabajadorId = jornada.trabajadorId,
                fecha = jornada.fecha,
                horaEntrada = jornada.horaEntrada,
                horaSalida = jornada.horaSalida,
                horasTrabajadas = jornada.horasTrabajadas,
                diaCompleto = jornada.diaCompleto,
                horasExtra = jornada.horasExtra,
                horasDebidas = jornada.horasDebidas,
                pagada = !jornada.pagada
            )
        )
    }

    suspend fun eliminarJornada(jornada: Jornada) {
        dao.eliminarJornada(
            JornadaEntity(
                id = jornada.id,
                trabajadorId = jornada.trabajadorId,
                fecha = jornada.fecha,
                horaEntrada = jornada.horaEntrada,
                horaSalida = jornada.horaSalida,
                horasTrabajadas = jornada.horasTrabajadas,
                diaCompleto = jornada.diaCompleto,
                horasExtra = jornada.horasExtra,
                horasDebidas = jornada.horasDebidas,
                pagada = jornada.pagada
            )
        )
    }
}