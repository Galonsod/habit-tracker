package com.gjad.remember.data

import kotlinx.coroutines.flow.Flow

interface RecordatorioRepositorio {

    suspend fun insertRecordatorio(recordatorio: Recordatorio)

    suspend fun deleteRecordatorio(recordatorio: Recordatorio)

    suspend fun updateRecordatorio(recordatorio: Recordatorio)

    suspend fun getRecordatorioById(id: Int): Recordatorio?

    suspend fun getRecordatorioByTime(formattedTime: String): Recordatorio?

    //suspend fun getRecordatorioByCategory(category: String): Recordatorio?

    //suspend fun getRecordatorioByProgress(progress: Boolean): Recordatorio?

    //suspend fun getRecordatorioLikeBuscado(buscado: String): Recordatorio?

    fun getRecordatorios(): Flow<List<Recordatorio>>
    fun getRecordatorioByDate(formattedDate: String): Flow<List<Recordatorio>>
}

interface TareaRepositorio {

    suspend fun insertTarea(tarea: Tarea)

    suspend fun deleteTarea(tarea: Tarea)

    suspend fun updateTarea(tarea: Tarea)

    suspend fun getTareaById(id: Int): Tarea?

    //suspend fun getTareaByDate(formattedDate: String): Tarea?

    suspend fun getTareaByTime(formattedTime: String): Tarea?

    //suspend fun getRecordatorioByCategory(category: String): Tarea?

    suspend fun getTareaByDeadLine(deadLine: Boolean): Tarea?

    //suspend fun getTareaLikeBuscado(buscado: String): Tarea?

    fun getTareas(): Flow<List<Tarea>>
    fun getTareaByDate(formattedDate: String): Flow<List<Tarea>>
    fun getTareaByProgress(progress: Int): Flow<List<Tarea>>
    fun getTareaByDateAndProgress(formattedDate: String, progress: Int): Flow<List<Tarea>>
}