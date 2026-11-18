package com.example.app_nueva_panchovilla.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [TrabajadorEntity::class, JornadaEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PanchoVillaDatabase : RoomDatabase() {

    abstract fun trabajoDao(): TrabajoDao

    companion object {
        @Volatile
        private var INSTANCE: PanchoVillaDatabase? = null

        fun getDatabase(context: Context): PanchoVillaDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    PanchoVillaDatabase::class.java,
                    "panchovilla_database"
                )
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}