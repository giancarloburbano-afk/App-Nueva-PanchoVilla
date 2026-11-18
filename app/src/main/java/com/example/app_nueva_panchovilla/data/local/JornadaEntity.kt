package com.example.app_nueva_panchovilla.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "jornadas",
    foreignKeys = [
        ForeignKey(
            entity = TrabajadorEntity::class,
            parentColumns = ["id"],
            childColumns = ["trabajadorId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["trabajadorId"])]
)
data class JornadaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val trabajadorId: Int,
    val fecha: String,
    val horaEntrada: Int,
    val horaSalida: Int,
    val horasTrabajadas: Int,
    val diaCompleto: Boolean,
    val horasExtra: Int,
    val horasDebidas: Int,
    val pagada: Boolean = false
)