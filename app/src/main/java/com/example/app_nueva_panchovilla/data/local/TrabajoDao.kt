package com.example.app_nueva_panchovilla.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TrabajoDao {

    // --- CRUD TRABAJADORES ---
    @Query("SELECT * FROM trabajadores ORDER BY nombre ASC")
    fun obtenerTrabajadores(): Flow<List<TrabajadorEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTrabajador(trabajador: TrabajadorEntity)

    @Update
    suspend fun actualizarTrabajador(trabajador: TrabajadorEntity)

    @Delete
    suspend fun eliminarTrabajador(trabajador: TrabajadorEntity)

    // --- CRUD JORNADAS ---
    @Query("SELECT * FROM jornadas ORDER BY id DESC")
    fun obtenerJornadas(): Flow<List<JornadaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarJornada(jornada: JornadaEntity)

    @Update
    suspend fun actualizarJornada(jornada: JornadaEntity)

    @Delete
    suspend fun eliminarJornada(jornada: JornadaEntity)
}