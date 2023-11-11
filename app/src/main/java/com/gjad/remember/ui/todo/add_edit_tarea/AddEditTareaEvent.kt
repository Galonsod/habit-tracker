package com.gjad.remember.ui.todo.add_edit_tarea

import androidx.room.PrimaryKey


sealed class AddEditTareaEvent {
    data class OnDateChange(val formattedDate: String): AddEditTareaEvent()
    data class OnTimeChange(val formattedTime: String): AddEditTareaEvent()
    data class OnCreationChange(val creationDate: String): AddEditTareaEvent()
    //data class OnCategoryChange(val category: Int): AddEditTareaEvent()
    data class OnTitleChange(val title: String): AddEditTareaEvent()
    data class OnDescriptionChange(val description: String): AddEditTareaEvent()
    data class OnProgressChange(val progress: Int): AddEditTareaEvent()
    data class OnDeadlineChange(val deadLine: Boolean): AddEditTareaEvent()
    //data class periodico ??? creacion automatica de tarea cada x tiempo
    object OnSaveTareaClick: AddEditTareaEvent()
    object OnCloseTareaClick: AddEditTareaEvent()
}