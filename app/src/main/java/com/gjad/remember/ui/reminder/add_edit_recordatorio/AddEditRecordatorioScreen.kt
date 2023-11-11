package com.gjad.remember.ui.reminder.add_edit_recordatorio

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.gjad.remember.util.UIEvent
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.TimePickerColors
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.LocalTime
import android.Manifest
import android.app.NotificationManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.core.app.NotificationCompat
import com.gjad.remember.R
import com.gjad.remember.ui.theme.DeepBlue
import com.gjad.remember.ui.theme.TextWhite
import com.gjad.remember.ui.todo.add_edit_tarea.AddEditTareaEvent
import com.gjad.remember.ui.todo.add_edit_tarea.ConfirmationButtons
import com.gjad.remember.ui.todo.add_edit_tarea.ViewModelAddEditTarea


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AddEditRecordatorioScreen(
    onPopBackStack: () -> Unit,
    viewModel: ViewModelAddEditRecordatorio = hiltViewModel()
) {

    ////////////////////////////////////////////////////////
    //Peticion de permiso para envio de notificaciones con API 33+
    val context = LocalContext.current
    var hasNotificationPermission by remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            )
        } else mutableStateOf(true)
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasNotificationPermission = isGranted
        }
    )

    //////////////////////////////////////////////////////////////

    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UIEvent.PopBackStack -> onPopBackStack()
                is UIEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.mensaje,
                        actionLabel = event.accion
                    )
                }
                else -> Unit
            }
        }
    }

    Scaffold(
        backgroundColor = DeepBlue,
        scaffoldState = scaffoldState,
        floatingActionButton = { ConfirmationButtons(viewModel) },
        floatingActionButtonPosition = FabPosition.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Creación de Recordatorio",
                style = MaterialTheme.typography.h1,
                modifier = Modifier.padding(15.dp)
            )
            TextField(
                value = viewModel.title,
                onValueChange = {
                    viewModel.onEvent(AddEditRecordatorioEvent.OnTitleChange(it))
                },
                placeholder = {
                    Text(text = "Título")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.size(8.dp))

            TextField(
                value = viewModel.description,
                onValueChange = {
                    viewModel.onEvent(AddEditRecordatorioEvent.OnDescriptionChange(it))
                },
                placeholder = {
                    Text(text = "Descripción")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                maxLines = 5
            )

            Spacer(modifier = Modifier.size(24.dp))

            Button(onClick = {
                dateDialogState.show()
            }) {
                Text(
                    text = "Escoge una fecha",
                    style = MaterialTheme.typography.h2,
                    color = TextWhite
                )
            }

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = viewModel.formattedDate,
                style = MaterialTheme.typography.h2,
                color = TextWhite
            )

            Spacer(modifier = Modifier.size(8.dp))

            Button(onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                if (hasNotificationPermission) {
                    timeDialogState.show()
                }
            }) {
                Text(
                    text = "Escoge una hora",
                    style = MaterialTheme.typography.h2,
                    color = TextWhite
                )
            }

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = viewModel.formattedTime,
                style = MaterialTheme.typography.h2,
                color = TextWhite
            )
        }

        //MaterialDialogCALENDARIO
        MaterialDialog(
            dialogState = dateDialogState,
            buttons = {
                positiveButton(text = "Ok") {

                }
                negativeButton(text = "Cancelar")
            }
        ) {
            datepicker(
                initialDate = LocalDate.now(),
                title = "Escoge una fecha",
                allowedDateValidator = {
                    it >= LocalDate.now()
                },
            ) {
                viewModel.pruebaDate(it)
            }
        }

        //MaterialDialogRELOJ
        MaterialDialog(
            dialogState = timeDialogState,
            buttons = {
                positiveButton(text = "Ok") {

                }
                negativeButton(text = "Cancelar")
            }
        ) {
            timepicker(
                initialTime = LocalTime.now(),
                title = "Escoge una hora",
                is24HourClock = true,
                timeRange = viewModel.limitarReloj
                //timeRange = LocalTime.now()..LocalTime.of(23,59) but if pickedDate != LocalDate.now(), full timeRange
            ) {
                viewModel.pruebaTime(it)
            }
        }
    }
}

@Composable
fun ConfirmationButtons(viewModel: ViewModelAddEditRecordatorio) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        FloatingActionButton(
            onClick = {
                viewModel.onEvent(AddEditRecordatorioEvent.OnCloseRecordatorioClick)
            },
            backgroundColor = Color.Gray,
            modifier = Modifier.padding(horizontal = 25.dp, vertical = 7.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cancelar",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }

        FloatingActionButton(
            onClick = {
                viewModel.onEvent(AddEditRecordatorioEvent.OnSaveRecordatorioClick)
            },
            modifier = Modifier.padding(horizontal = 25.dp, vertical = 7.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Crear",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}


