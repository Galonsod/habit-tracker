package com.gjad.remember.ui.todo.tarea_list

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gjad.remember.ui.bottom_menu.BottomMenu
import com.gjad.remember.ui.reminder.recordatorio_list.RecordatorioItem
import com.gjad.remember.ui.reminder.recordatorio_list.RecordatorioListEvent
import com.gjad.remember.ui.reminder.recordatorio_list.ViewModelRecordatorioList
import com.gjad.remember.ui.theme.DeepBlue
import com.gjad.remember.util.UIEvent
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun TareaListScreen(
    onNavigate: (UIEvent.Navigate) -> Unit,
    viewModelTarea: ViewModelTareaList = hiltViewModel(),
    viewModelRecordatorio: ViewModelRecordatorioList = hiltViewModel()
) {
    val tareasToday = viewModelTarea.tareasToday.collectAsState(initial = emptyList())
    val tareasToDo = viewModelTarea.tareasToDo.collectAsState(initial = emptyList())
    val tareasWin = viewModelTarea.tareasWin.collectAsState(initial = emptyList())
    val tareasFail = viewModelTarea.tareasFail.collectAsState(initial = emptyList())

    val scaffoldState = rememberScaffoldState()
    LaunchedEffect(key1 = true) {
        viewModelTarea.uiEvent.collect { event ->
            when (event) {
                is UIEvent.ShowSnackbar -> {
                    val result = scaffoldState.snackbarHostState.showSnackbar(
                        message = event.mensaje,
                        actionLabel = event.accion
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModelTarea.onEvent(TareaListEvent.OnUndoDeleteClick)
                    }
                }
                is UIEvent.Navigate -> onNavigate(event)
                else -> Unit
            }
        }
    }
    LaunchedEffect(key1 = true) {
        viewModelRecordatorio.uiEvent.collect { event ->
            when (event) {
                is UIEvent.ShowSnackbar -> {
                    val result = scaffoldState.snackbarHostState.showSnackbar(
                        message = event.mensaje,
                        actionLabel = event.accion
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModelRecordatorio.onEvent(RecordatorioListEvent.OnUndoDeleteClick)
                    }
                }
                is UIEvent.Navigate -> onNavigate(event)
                else -> Unit
            }
        }
    }

    Scaffold(
        backgroundColor = DeepBlue,
        scaffoldState = scaffoldState
    ) {
        Column(Modifier.fillMaxSize()) {
            TareaTopBar()
            LazyColumn(
            ) {
                item {
                    Column(
                        modifier = Modifier.padding(15.dp)
                    ) {
                        Text(
                            text = "Tareas para hoy",
                            style = MaterialTheme.typography.h1,
                            modifier = Modifier.padding(bottom = 15.dp)
                        )
                        if (tareasToday.value.isEmpty()) {
                            Text(
                                text = "Hoy no queda nada por hacer.",
                                style = MaterialTheme.typography.body1,
                                modifier = Modifier.padding(bottom = 15.dp)
                            )
                        }
                    }
                }

                items(tareasToday.value) { tarea ->
                    TareaItem(
                        tarea = tarea,
                        onEvent = viewModelTarea::onEvent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }

                item {
                    Column(
                        modifier = Modifier.padding(15.dp)
                    ) {
                        Text(
                            text = "Todas las tareas por hacer",
                            style = MaterialTheme.typography.h1,
                            modifier = Modifier.padding(bottom = 15.dp)
                        )
                        if (tareasToDo.value.isEmpty()) {
                            Text(
                                text = "No hay tareas por hacer. Crea una nueva tarea.",
                                style = MaterialTheme.typography.body1,
                                modifier = Modifier.padding(bottom = 15.dp)
                            )
                        }
                    }
                }

                items(tareasToDo.value) { tarea ->
                    TareaItem(
                        tarea = tarea,
                        onEvent = viewModelTarea::onEvent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
                item {
                    Column(
                        modifier = Modifier.padding(15.dp)
                    ) {
                        Text(
                            text = "Tareas Completadas",
                            style = MaterialTheme.typography.h1,
                            modifier = Modifier.padding(bottom = 15.dp)
                        )
                        if (tareasWin.value.isEmpty()) {
                            Text(
                                text = "No hay tareas completadas. ¡Llena la lista!",
                                style = MaterialTheme.typography.body1,
                                modifier = Modifier.padding(bottom = 15.dp)
                            )
                        }
                    }
                }

                items(tareasWin.value) { tarea ->
                    TareaItem(
                        tarea = tarea,
                        onEvent = viewModelTarea::onEvent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
                item {
                    Column(
                        modifier = Modifier.padding(15.dp)
                    ) {
                        Text(
                            text = "Tareas Fallidas",
                            style = MaterialTheme.typography.h1,
                            modifier = Modifier.padding(bottom = 15.dp)
                        )
                        if (tareasFail.value.isEmpty()) {
                            Text(
                                text = "No hay tareas fallidas. ¡Sigue así!",
                                style = MaterialTheme.typography.body1,
                                modifier = Modifier.padding(bottom = 15.dp)
                            )
                        }
                    }
                }

                items(tareasFail.value) { tarea ->
                    TareaItem(
                        tarea = tarea,
                        onEvent = viewModelTarea::onEvent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
                item {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .background(DeepBlue)
                            .padding(48.dp),
                        //.padding(bottom = 24.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                    }
                }
            }
        }
        Column(verticalArrangement = Arrangement.Bottom) {
            BottomMenu()
        }
    }
}

@Composable
fun TareaTopBar() {
    Column(
        modifier = Modifier.padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "To Do",
            style = MaterialTheme.typography.h1,
            modifier = Modifier.padding(bottom = 15.dp)
        )
    }
}