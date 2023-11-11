package com.gjad.remember.ui.reminder.recordatorio_list

import com.gjad.remember.data.Recordatorio
import com.gjad.remember.ui.todo.tarea_list.TareaListEvent


sealed class RecordatorioListEvent {
    data class OnDeleteRecordatorioClick(val recordatorio: Recordatorio, val mensaje: String) : RecordatorioListEvent()
    data class OnProgressChange(val recordatorio: Recordatorio, val progress: Boolean?) :
        RecordatorioListEvent()

    object OnUndoDeleteClick : RecordatorioListEvent()
    data class OnRecordatorioClick(val recordatorio: Recordatorio) : RecordatorioListEvent()


    object OnAddRecordatorioClick : RecordatorioListEvent()
    object OnRecordatorioListMenu : RecordatorioListEvent()
    object OnProfileMenu : RecordatorioListEvent()
}