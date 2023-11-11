package com.gjad.remember.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordatorioDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)   //evita problemas por repeticion de IDs
    suspend fun insertRecordatorio(recordatorio: Recordatorio)

    @Delete
    suspend fun deleteRecordatorio(recordatorio: Recordatorio)

    @Update
    suspend fun updateRecordatorio(recordatorio: Recordatorio)

    @Query("SELECT * FROM recordatorio WHERE id = :id")
    suspend fun getRecordatorioById(id: Int): Recordatorio?

    @Query("SELECT * FROM recordatorio WHERE formattedTime = :formattedTime")
    suspend fun getRecordatorioByTime(formattedTime: String): Recordatorio?

    //@Query("SELECT * FROM recordatorio WHERE category = :category")
    //suspend fun getRecordatorioByCategory(category: String): Recordatorio?

    /*@Query("SELECT * FROM recordatorio WHERE progress = :progress")
    suspend fun getRecordatorioByProgress(progress: Boolean): Recordatorio?

    @Query("SELECT * FROM recordatorio WHERE title LIKE '%buscado%' OR description LIKE '%buscado%'")
    suspend fun getRecordatorioLikeBuscado(buscado: String): Recordatorio?*/

    @Query("SELECT * FROM recordatorio")
    fun getRecordatorios(): Flow<List<Recordatorio>>

    @Query("SELECT * FROM recordatorio WHERE formattedDate = :formattedDate")
    fun getRecordatorioByDate(formattedDate: String): Flow<List<Recordatorio>>
}

@Dao
interface TareaDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTarea(tarea: Tarea)

    @Delete
    suspend fun deleteTarea(tarea: Tarea)

    @Update
    suspend fun updateTarea(tarea: Tarea)

    @Query("SELECT * FROM tarea WHERE id = :id")
    suspend fun getTareaById(id: Int): Tarea?

    //@Query("SELECT * FROM tarea WHERE formattedDate = :formattedDate")
    //suspend fun getTareaByDate(formattedDate: String): Tarea?

    @Query("SELECT * FROM tarea WHERE formattedTime = :formattedTime")
    suspend fun getTareaByTime(formattedTime: String): Tarea?

    //@Query("SELECT * FROM tarea WHERE category = :category")
    //suspend fun getTareaByCategory(category: String): Tarea?

    @Query("SELECT * FROM tarea WHERE deadLine = :deadLine")
    suspend fun getTareaByDeadLine(deadLine: Boolean): Tarea?

    /*@Query("SELECT * FROM tarea WHERE title LIKE '%buscado%' OR description LIKE '%buscado%'")
    suspend fun getTareaLikeBuscado(buscado: String): Tarea?*/

    @Query("SELECT * FROM tarea")
    fun getTareas(): Flow<List<Tarea>>

    @Query("SELECT * FROM tarea WHERE formattedDate = :formattedDate")
    fun getTareaByDate(formattedDate: String): Flow<List<Tarea>>

    @Query("SELECT * FROM tarea WHERE progress = :progress")
    fun getTareaByProgress(progress: Int): Flow<List<Tarea>>

    @Query("SELECT * FROM tarea WHERE formattedDate = :formattedDate AND progress = :progress")
    fun getTareaByDateAndProgress(formattedDate: String, progress: Int): Flow<List<Tarea>>
}

