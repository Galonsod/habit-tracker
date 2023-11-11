package com.gjad.remember.data

import kotlinx.coroutines.flow.Flow

class RecordatorioRepositorioImplementacion (
    private val dao: RecordatorioDAO
): RecordatorioRepositorio {

    override suspend fun insertRecordatorio(recordatorio: Recordatorio) {
        dao.insertRecordatorio(recordatorio)
    }

    override suspend fun deleteRecordatorio(recordatorio: Recordatorio) {
        dao.deleteRecordatorio(recordatorio)
    }

    override suspend fun updateRecordatorio(recordatorio: Recordatorio) {
        dao.updateRecordatorio(recordatorio)
    }

    override suspend fun getRecordatorioById(id: Int): Recordatorio? {
        return dao.getRecordatorioById(id)
    }

    override suspend fun getRecordatorioByTime(formattedTime: String): Recordatorio? {
        return dao.getRecordatorioByTime(formattedTime)
    }

    /*override suspend fun getRecordatorioByCategory(category: String): Recordatorio? {
        return dao.getRecordatorioByCategory(category)
    }

    override suspend fun getRecordatorioByProgress(progress: Boolean): Recordatorio? {
        return dao.getRecordatorioByProgress(progress)
    }*/

    /*override suspend fun getRecordatorioLikeBuscado(buscado: String): Recordatorio? {
        return dao.getRecordatorioLikeBuscado(buscado)
    }*/

    override fun getRecordatorios(): Flow<List<Recordatorio>> {
        return dao.getRecordatorios()
    }

    override fun getRecordatorioByDate(formattedDate: String): Flow<List<Recordatorio>> {
        return dao.getRecordatorioByDate(formattedDate)
    }
}

class TareaRepositorioImplementacion (
    private val dao: TareaDAO
): TareaRepositorio {

    override suspend fun insertTarea(tarea: Tarea) {
        dao.insertTarea(tarea)
    }

    override suspend fun deleteTarea(tarea: Tarea) {
        dao.deleteTarea(tarea)
    }

    override suspend fun updateTarea(tarea: Tarea) {
        dao.updateTarea(tarea)
    }

    override suspend fun getTareaById(id: Int): Tarea? {
        return dao.getTareaById(id)
    }

    /*override suspend fun getTareaByDate(formattedDate: String): Tarea? {
        return dao.getTareaByDate(formattedDate)
    }*/

    override suspend fun getTareaByTime(formattedTime: String): Tarea? {
        return dao.getTareaByTime(formattedTime)
    }

    /*override suspend fun getTareaByCategory(category: String): Tarea? {
        return dao.getTareaByCategory(category)
    }*/

    override suspend fun getTareaByDeadLine(deadLine: Boolean): Tarea? {
        return dao.getTareaByDeadLine(deadLine)
    }

    /*override suspend fun getTareaLikeBuscado(buscado: String): Tarea? {
        return dao.getTareaLikeBuscado(buscado)
    }*/

    override fun getTareas(): Flow<List<Tarea>> {
        return dao.getTareas()
    }

    override fun getTareaByDate(formattedDate: String): Flow<List<Tarea>> {
        return dao.getTareaByDate(formattedDate)
    }

    override fun getTareaByProgress(progress: Int): Flow<List<Tarea>> {
        return dao.getTareaByProgress(progress)
    }

    override fun getTareaByDateAndProgress(formattedDate: String, progress: Int): Flow<List<Tarea>> {
        return dao.getTareaByDateAndProgress(formattedDate, progress)
    }
}