package com.gjad.remember.ui.todo.tarea_list

import com.gjad.remember.data.Tarea


sealed class TareaListEvent {
    data class OnDeleteTareaClick(val tarea: Tarea, val mensaje: String) : TareaListEvent()
    data class OnProgressChange(val tarea: Tarea, val progress: Int, val mensaje: String) :
        TareaListEvent()

    object OnUndoDeleteClick : TareaListEvent()
    data class OnTareaClick(val tarea: Tarea) : TareaListEvent()


    object OnAddTareaClick : TareaListEvent()

    object OnTareaListMenu : TareaListEvent()
    object OnHomeMenu : TareaListEvent()
}