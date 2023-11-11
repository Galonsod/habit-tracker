package com.gjad.remember.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Recordatorio::class, Tarea::class],
    version = 1,
    exportSchema = true //para exportar la DB, para compartir / descargar recordatorios
)
abstract class EntidadesDB: RoomDatabase() {
    abstract val recordatoriodao: RecordatorioDAO
    abstract val tododao: TareaDAO
}