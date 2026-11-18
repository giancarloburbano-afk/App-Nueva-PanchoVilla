package com.example.app_nueva_panchovilla.model

data class Trabajador(
    val id: Int = 0,
    val nombre: String,
    val cargo: String,
    val pagoPorDia: Double
)

data class Jornada(
    val id: Int = 0,
    val trabajadorId: Int,
    val nombreTrabajador: String = "",
    val fecha: String,
    val horaEntrada: Int, // Ej: 7 (7:00 a. m.)
    val horaSalida: Int,  // Ej: 15 (3:00 p. m.)
    val horasTrabajadas: Int,
    val diaCompleto: Boolean,
    val horasExtra: Int,
    val horasDebidas: Int,
    val pagada: Boolean = false
)

enum class FiltroJornada(val texto: String) {
    TODAS("Todas"),
    HORAS_EXTRA("Con horas extra"),
    DEBEN_HORAS("Deben horas"),
    PENDIENTES("Sin pagar")
}

data class CalculoTurno(
    val horasTrabajadas: Int,
    val diaCompleto: Boolean,
    val horasExtra: Int,
    val horasDebidas: Int
)

// Regla de negocio: jornada base de 7:00 a. m. (7) a 3:00 p. m. (15) = 8 horas
fun calcularTurno(horaEntrada: Int, horaSalida: Int): CalculoTurno {
    val trabajadas = (horaSalida - horaEntrada).coerceAtLeast(0)
    val esDiaCompleto = trabajadas >= 8
    val extra = if (trabajadas > 8) trabajadas - 8 else 0
    val debidas = if (trabajadas < 8) 8 - trabajadas else 0
    return CalculoTurno(
        horasTrabajadas = trabajadas,
        diaCompleto = esDiaCompleto,
        horasExtra = extra,
        horasDebidas = debidas
    )
}

fun formatearHora(hora24: Int): String {
    return when {
        hora24 == 0 || hora24 == 24 -> "12:00 a. m."
        hora24 < 12 -> "$hora24:00 a. m."
        hora24 == 12 -> "12:00 p. m."
        else -> "${hora24 - 12}:00 p. m."
    }
}