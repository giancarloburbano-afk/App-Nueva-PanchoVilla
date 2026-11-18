package com.example.app_nueva_panchovilla.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trabajadores")
data class TrabajadorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val cargo: String,
    val pagoPorDia: Double
)