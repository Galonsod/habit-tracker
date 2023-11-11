package com.gjad.remember.ui.reminder.add_edit_recordatorio

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import com.gjad.remember.data.Recordatorio
import com.gjad.remember.data.RecordatorioRepositorio
import com.gjad.remember.util.UIEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject


@HiltViewModel
class ViewModelAddEditRecordatorio @Inject constructor(
    private val repositorio: RecordatorioRepositorio,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

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

    var recordatorio by mutableStateOf<Recordatorio?>(null)
        private set

    var formattedDate by mutableStateOf(
        LocalDate.now().format(DateTimeFormatter.ofPattern("dd / MMM / yyyy"))
    )
        private set

    var formattedTime by mutableStateOf("23 : 59")
        private set

    var category by mutableStateOf("")
        private set

    var title by mutableStateOf("")
        private set

    var description by mutableStateOf("")
        private set

    private val _uiEvent = Channel<UIEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        val recordatorioId = savedStateHandle.get<Int>("recordatorioId")!!
        if (recordatorioId != -1) {
            viewModelScope.launch {
                repositorio.getRecordatorioById(recordatorioId)?.let { recordatorio ->
                    formattedDate = recordatorio.formattedDate
                    formattedTime = recordatorio.formattedTime
                    //category = recordatorio.category ?:""
                    title = recordatorio.title
                    description = recordatorio.description
                    this@ViewModelAddEditRecordatorio.recordatorio = recordatorio
                }
            }
        }
    }

    fun onEvent(event: AddEditRecordatorioEvent) {
        when (event) {
            ///detectar cambios tiempo y fecha
            is AddEditRecordatorioEvent.OnDateChange -> {
                formattedDate = event.formattedDate
            }
            is AddEditRecordatorioEvent.OnTimeChange -> {
                formattedTime = event.formattedTime
            }
            /*is AddEditRecordatorioEvent.OnCategoryChange -> {
                category = event.category
            }*/
            is AddEditRecordatorioEvent.OnTitleChange -> {
                title = event.title
            }
            is AddEditRecordatorioEvent.OnDescriptionChange -> {
                description = event.description
            }
            is AddEditRecordatorioEvent.OnSaveRecordatorioClick -> {
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

                    repositorio.insertRecordatorio(
                        Recordatorio(
                            formattedDate = formattedDate,
                            formattedTime = formattedTime,
                            //category = recordatorio?.category,
                            title = title,
                            description = description,
                            //progress = recordatorio?.progress ?: false,
                            id = recordatorio?.id
                        )
                    )

                    sendUiEvent(UIEvent.PopBackStack)
                }
            }
            is AddEditRecordatorioEvent.OnCloseRecordatorioClick -> {
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