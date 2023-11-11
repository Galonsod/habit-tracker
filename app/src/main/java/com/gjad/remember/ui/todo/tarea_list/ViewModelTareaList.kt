package com.gjad.remember.ui.todo.tarea_list


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gjad.remember.data.Tarea
import com.gjad.remember.data.TareaRepositorio
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
class ViewModelTareaList @Inject constructor(
    private val repositorio: TareaRepositorio
) : ViewModel() {

    fun convertirFechaStringAFechaLocal(dateString: String?): LocalDate? {
        if (dateString.isNullOrEmpty()) {
            return null
        }
        val formatter = DateTimeFormatter.ofPattern("dd / MMM / yyyy")
        return LocalDate.parse(dateString, formatter)
    }

    fun convertirHoraStringAHoraLocal(timeString: String?): LocalTime? {
        if (timeString.isNullOrEmpty()) {
            return null
        }
        val formatter = DateTimeFormatter.ofPattern("HH : mm")
        return LocalTime.parse(timeString, formatter)
    }

    val tareasToDo = repositorio.getTareaByProgress(0)
        .map { tareas ->
            tareas.sortedWith(compareBy(
                { convertirFechaStringAFechaLocal(it.formattedDate) },
                { convertirHoraStringAHoraLocal(it.formattedTime) },
                { it.creationDate }
            ))
        }

    val tareasWin = repositorio.getTareaByProgress(1)
        .map { tareas ->
            tareas.sortedWith(compareBy(
                { convertirFechaStringAFechaLocal(it.formattedDate) },
                { convertirHoraStringAHoraLocal(it.formattedTime) },
                { it.creationDate }
            ))
        }


    val tareasFail = repositorio.getTareaByProgress(2)
        .map { tareas ->
            tareas.sortedWith(compareBy(
                { convertirFechaStringAFechaLocal(it.formattedDate) },
                { convertirHoraStringAHoraLocal(it.formattedTime) },
                { it.creationDate }
            ))
        }

    val tareasToday = repositorio.getTareaByDateAndProgress(LocalDate.now().format(DateTimeFormatter.ofPattern("dd / MMM / yyyy")), 0)

    private val _uiEvent = Channel<UIEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var deletedTarea: Tarea? = null

    fun onEvent(event: TareaListEvent) {
        when (event) {
            is TareaListEvent.OnTareaListMenu -> {
                sendUiEvent(UIEvent.Navigate(Rutas.TODO_LIST))
            }
            is TareaListEvent.OnHomeMenu -> {
                sendUiEvent(UIEvent.Navigate(Rutas.HOME))
            }





            is TareaListEvent.OnTareaClick -> {
                sendUiEvent(UIEvent.Navigate(Rutas.ADD_EDIT_TODO + "?tareaId=${event.tarea.id}"))
            }
            is TareaListEvent.OnAddTareaClick -> {
                sendUiEvent(UIEvent.Navigate(Rutas.ADD_EDIT_TODO))
            }
            is TareaListEvent.OnUndoDeleteClick -> {
                deletedTarea?.let { tarea ->
                    viewModelScope.launch {
                        repositorio.insertTarea(tarea)
                    }
                }
            }
            is TareaListEvent.OnDeleteTareaClick -> {
                viewModelScope.launch {
                    deletedTarea = event.tarea
                    repositorio.deleteTarea(event.tarea)
                    sendUiEvent(
                        UIEvent.ShowSnackbar(
                            mensaje = event.mensaje,
                            accion = "Deshacer"
                        )
                    )
                }
            }
            is TareaListEvent.OnProgressChange -> {
                viewModelScope.launch {
                    repositorio.insertTarea(
                        event.tarea.copy(
                            progress = event.progress
                        )
                    )
                    sendUiEvent(
                        UIEvent.ShowSnackbar(
                            mensaje = event.mensaje
                        )
                    )
                }
            }
            else -> {}
        }
    }

    private fun sendUiEvent(event: UIEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}