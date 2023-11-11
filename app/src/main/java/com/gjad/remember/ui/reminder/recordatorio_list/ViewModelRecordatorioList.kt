package com.gjad.remember.ui.reminder.recordatorio_list


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gjad.remember.data.Recordatorio
import com.gjad.remember.data.RecordatorioRepositorio
import com.gjad.remember.ui.todo.tarea_list.TareaListEvent
import com.gjad.remember.util.Rutas
import com.gjad.remember.util.UIEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ViewModelRecordatorioList @Inject constructor(
    private val repositorio: RecordatorioRepositorio
) : ViewModel() {

    fun convertirFechaStringAFechaLocal(dateString: String): LocalDate {
        val formatter = DateTimeFormatter.ofPattern("dd / MMM / yyyy")
        return LocalDate.parse(dateString, formatter)
    }

    fun convertirHoraStringAHoraLocal(timeString: String): LocalTime {
        val formatter = DateTimeFormatter.ofPattern("HH : mm")
        return LocalTime.parse(timeString, formatter)
    }

    val recordatoriosAll = repositorio.getRecordatorios()
        .map { recordatorios ->
            recordatorios.sortedWith(compareBy(
                { convertirFechaStringAFechaLocal(it.formattedDate) },
                { convertirHoraStringAHoraLocal(it.formattedTime) }
            ))
        }



    val recordatoriosToday = repositorio.getRecordatorioByDate(
        LocalDate.now().format(
            DateTimeFormatter.ofPattern("dd / MMM / yyyy")))

    private val _uiEvent = Channel<UIEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var deletedRecordatorio: Recordatorio? = null

    fun onEvent(event: RecordatorioListEvent) {
        when (event) {
            is RecordatorioListEvent.OnRecordatorioListMenu -> {
                sendUiEvent(UIEvent.Navigate(Rutas.REMINDER_LIST))
            }
            is RecordatorioListEvent.OnProfileMenu -> {
                sendUiEvent(UIEvent.Navigate(Rutas.PROFILE))
            }


            is RecordatorioListEvent.OnRecordatorioClick -> {
                sendUiEvent(UIEvent.Navigate(Rutas.ADD_EDIT_REMINDER + "?recordatorioId=${event.recordatorio.id}"))
            }
            is RecordatorioListEvent.OnAddRecordatorioClick -> {
                sendUiEvent(UIEvent.Navigate(Rutas.ADD_EDIT_REMINDER))
            }
            is RecordatorioListEvent.OnUndoDeleteClick -> {
                deletedRecordatorio?.let { recordatorio ->
                    viewModelScope.launch {
                        repositorio.insertRecordatorio(recordatorio)
                    }
                }
            }
            is RecordatorioListEvent.OnDeleteRecordatorioClick -> {
                viewModelScope.launch {
                    deletedRecordatorio = event.recordatorio
                    repositorio.deleteRecordatorio(event.recordatorio)
                    sendUiEvent(
                        UIEvent.ShowSnackbar(
                            mensaje = event.mensaje,
                            accion = "Deshacer"
                        )
                    )
                }
            }


            /*is RecordatorioListEvent.OnProgressChange -> {
                viewModelScope.launch {
                    repositorio.insertRecordatorio(
                        event.recordatorio.copy(
                            progress = event.progress
                        )
                    )
                }
            }*/
            else -> {}
        }
    }

    private fun sendUiEvent(event: UIEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}