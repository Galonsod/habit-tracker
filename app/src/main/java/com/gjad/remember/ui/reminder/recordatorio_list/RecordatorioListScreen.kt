package com.gjad.remember.ui.reminder.recordatorio_list

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gjad.remember.ui.bottom_menu.BottomMenu
import com.gjad.remember.ui.theme.DeepBlue
import com.gjad.remember.ui.todo.tarea_list.TareaItem
import com.gjad.remember.ui.todo.tarea_list.TareaListEvent
import com.gjad.remember.ui.todo.tarea_list.ViewModelTareaList
import com.gjad.remember.util.UIEvent

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun RecordatorioListScreen(
    onNavigate: (UIEvent.Navigate) -> Unit,
    viewModelRecordatorio: ViewModelRecordatorioList = hiltViewModel(),
    viewModelTarea: ViewModelTareaList = hiltViewModel()
) {
    val recordatoriosToday = viewModelRecordatorio.recordatoriosToday.collectAsState(initial = emptyList())
    val recordatoriosAll = viewModelRecordatorio.recordatoriosAll.collectAsState(initial = emptyList())
    val scaffoldState = rememberScaffoldState()
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
    LaunchedEffect(key1 = true) {
        viewModelTarea.uiEvent.collect { event ->
            when (event) {
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
            RecordatorioTopBar()
            LazyColumn(
            ) {
                item {
                    Column(
                        modifier = Modifier.padding(15.dp)
                    ) {
                        Text(
                            text = "Recordatorios para hoy",
                            style = MaterialTheme.typography.h1,
                            modifier = Modifier.padding(bottom = 15.dp)
                        )
                        if (recordatoriosToday.value.isEmpty()) {
                            Text(
                                text = "No hay recordatorios hoy.",
                                style = MaterialTheme.typography.body1,
                                modifier = Modifier.padding(bottom = 15.dp)
                            )
                        }
                    }
                }

                items(recordatoriosToday.value) { recordatorio ->
                    RecordatorioItem(
                        recordatorio = recordatorio,
                        onEvent = viewModelRecordatorio::onEvent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModelRecordatorio.onEvent(
                                    RecordatorioListEvent.OnRecordatorioClick(
                                        recordatorio
                                    )
                                )
                            }
                            .padding(16.dp)
                    )
                }

                item {
                    Column(
                        modifier = Modifier.padding(15.dp)
                    ) {
                        Text(
                            text = "Todos los recordatorios pendientes",
                            style = MaterialTheme.typography.h1,
                            modifier = Modifier.padding(bottom = 15.dp)
                        )
                        if (recordatoriosAll.value.isEmpty()) {
                            Text(
                                text = "No hay recordatorios pendientes. Crea un nuevo recordatorio.",
                                style = MaterialTheme.typography.body1,
                                modifier = Modifier.padding(bottom = 15.dp)
                            )
                        }
                    }
                }

                items(recordatoriosAll.value) { recordatorio ->
                    RecordatorioItem(
                        recordatorio = recordatorio,
                        onEvent = viewModelRecordatorio::onEvent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModelRecordatorio.onEvent(RecordatorioListEvent.OnRecordatorioClick(recordatorio))
                            }
                            .padding(16.dp)
                    )
                }
                item {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .background(DeepBlue)
                            .padding(48.dp),
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
fun RecordatorioTopBar() {
    Column(
        modifier = Modifier.padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Remind",
            style = MaterialTheme.typography.h1,
            modifier = Modifier.padding(bottom = 15.dp)
        )
    }
}