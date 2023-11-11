package com.gjad.remember.ui.reminder.add_edit_recordatorio

sealed class AddEditRecordatorioEvent {
    data class OnDateChange(val formattedDate: String): AddEditRecordatorioEvent()
    data class OnTimeChange(val formattedTime: String): AddEditRecordatorioEvent()
    //data class OnCategoryChange(val category: String): AddEditRecordatorioEvent()
    data class OnTitleChange(val title: String): AddEditRecordatorioEvent()
    data class OnDescriptionChange(val description: String): AddEditRecordatorioEvent()
    object OnSaveRecordatorioClick: AddEditRecordatorioEvent()
    object OnCloseRecordatorioClick: AddEditRecordatorioEvent()
}