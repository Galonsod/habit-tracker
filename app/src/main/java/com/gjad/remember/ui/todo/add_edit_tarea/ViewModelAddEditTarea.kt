package com.gjad.remember.ui.todo.add_edit_tarea

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import com.gjad.remember.data.Tarea
import com.gjad.remember.data.TareaRepositorio
import com.gjad.remember.util.UIEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi

@HiltViewModel
class ViewModelAddEditTarea @Inject constructor(
    private val repositorio: TareaRepositorio,
    savedStateHandle: SavedStateHandle
) : ViewModel() {


    ///////////////////////////////////////////////////////////

    var isTooltipVisible by mutableStateOf(false)
    var pickedDate by mutableStateOf<LocalDate?>(null)
        private set
    var pickedTime by mutableStateOf<LocalTime?>(null)
        private set
    var limitarReloj: ClosedRange<LocalTime> = LocalTime.now()..LocalTime.of(23, 59)
        private set

    fun pruebaDate(it: LocalDate) {
        pickedDate = it
        formattedDate = it.format(DateTimeFormatter.ofPattern("dd / MMM / yyyy"))
        if (pickedDate != LocalDate.now()) {
            limitarReloj = LocalTime.MIN..LocalTime.MAX
        }
    }

    fun pruebaTime(it: LocalTime) {
        pickedTime = it
        formattedTime = it.format((DateTimeFormatter.ofPattern("HH : mm")))
    }

    /*
    it es el valor del tiempo LocalTime.now() y cambia cuando se escoge otro. Al pulsar OK en el dialog del tiempo, pickedTime
    coge el valor de it. Es necesario que it se actualice con una funcion que podria ser un bucle while que, en la primera iteracion,
    el tiempo se actualiza pasados los 60 - LocalTime.now().second segundos, y se ejecutará cada 60 segundos despues.
    Una vez se pulse "OK" en el pickerTime, la funcion se suspende (el bucle se cierra) y el tiempo deja de actualizarse.
    Si esto requiere corutina, es posible que sea mejor actualizar el tiempo con dicha corutina al detectar cambio de hora y ya,
    pero en teoria mi forma gasta menos recursos
     */

    var tarea by mutableStateOf<Tarea?>(null)
        private set

    var formattedDate by mutableStateOf("")
        private set

    var formattedTime by mutableStateOf("")
        private set

    var creationDate by mutableStateOf(LocalDate.now().format(DateTimeFormatter.ofPattern("dd / MMM / yyyy")))
        private set

    var category by mutableStateOf("")
        private set

    var title by mutableStateOf("")
        private set

    var description by mutableStateOf("")
        private set

    var progress by mutableStateOf(0)
        private set

    var deadLine by mutableStateOf(false)
        private set

    private val _uiEvent = Channel<UIEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        val tareaId = savedStateHandle.get<Int>("tareaId")!!
        if (tareaId != -1) {
            viewModelScope.launch {
                repositorio.getTareaById(tareaId)?.let { tarea ->
                    formattedDate = tarea.formattedDate
                    formattedTime = tarea.formattedTime
                    creationDate = tarea.creationDate
                    title = tarea.title
                    description = tarea.description
                    progress = tarea.progress
                    deadLine = tarea.deadLine
                    this@ViewModelAddEditTarea.tarea = tarea
                }
            }
        }
    }

    fun onEvent(event: AddEditTareaEvent) {
        when (event) {
            ///detectar cambios tiempo y fecha
            is AddEditTareaEvent.OnDateChange -> {
                formattedDate = event.formattedDate
            }
            is AddEditTareaEvent.OnTimeChange -> {
                formattedTime = event.formattedTime
            }
            is AddEditTareaEvent.OnCreationChange -> {
                creationDate = event.creationDate
            }
            is AddEditTareaEvent.OnProgressChange -> {
                progress = event.progress
            }
            is AddEditTareaEvent.OnDeadlineChange -> {
                deadLine = event.deadLine
            }
            is AddEditTareaEvent.OnTitleChange -> {
                title = event.title
            }
            is AddEditTareaEvent.OnDescriptionChange -> {
                description = event.description
            }
            is AddEditTareaEvent.OnSaveTareaClick -> {
                viewModelScope.launch {
                    if (title.isBlank()) {
                        sendUiEvent(
                            UIEvent.ShowSnackbar(
                                mensaje = "El título no puede quedar vacío."
                            )
                        )
                        return@launch
                    }
                    if (title.length > 25) {
                        sendUiEvent(
                            UIEvent.ShowSnackbar(
                                mensaje = "El título no puede tener más de 25 caracteres."
                            )
                        )
                        return@launch
                    }

                    /*if(formattedTime.isBlank()){
                        sendUiEvent(UIEvent.ShowSnackbar(
                            mensaje = "Tarea creada correctamente."
                        ))
                    } else {
                        sendUiEvent(UIEvent.ShowSnackbar(
                            mensaje = "Recordatorio creado correctamente."
                        ))
                    }*/

                    repositorio.insertTarea(
                        Tarea(
                            formattedDate = formattedDate,
                            formattedTime = formattedTime,
                            creationDate = creationDate,
                            title = title,
                            description = description,
                            progress = progress,
                            deadLine = deadLine,
                            id = tarea?.id
                        )
                    )

                    sendUiEvent(UIEvent.PopBackStack)
                }
            }
            is AddEditTareaEvent.OnCloseTareaClick -> {
                sendUiEvent(UIEvent.PopBackStack)
            }
        }
    }

    private fun sendUiEvent(event: UIEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}