package com.gjad.remember.ui.todo.add_edit_tarea

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
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.gjad.remember.util.UIEvent
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.LocalTime
import android.Manifest
import android.app.usage.UsageEvents.Event
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.ViewModel
import com.gjad.remember.ui.home.TarjetaItem
import com.gjad.remember.ui.tarjeta.Tarjeta
import com.gjad.remember.ui.theme.DeepBlue
import com.gjad.remember.ui.theme.TextWhite


//@RequiresApi(Build.VERSION_CODES.P)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AddEditTareaScreen(
    onPopBackStack: () -> Unit,
    viewModel: ViewModelAddEditTarea = hiltViewModel(),

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
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Creación de Tarea",
                style = MaterialTheme.typography.h1,
                modifier = Modifier.padding(15.dp)
            )
            TextField(
                value = viewModel.title,
                onValueChange = {
                    viewModel.onEvent(AddEditTareaEvent.OnTitleChange(it))
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
                    viewModel.onEvent(AddEditTareaEvent.OnDescriptionChange(it))
                },
                placeholder = {
                    Text(text = "Descripción")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                maxLines = 5
            )

            Spacer(modifier = Modifier.size(16.dp))

            Text(
                text = "¿Quieres un recordatorio de la tarea?",
                style = MaterialTheme.typography.h2,
                modifier = Modifier.padding(15.dp)
            )

            Spacer(modifier = Modifier.size(8.dp))

            Button(onClick = {
                dateDialogState.show()
            }) {
                Text(
                    text = "Escoge una fecha",
                    style = MaterialTheme.typography.h2,
                    color = TextWhite)
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Checkbox(
                    checked = viewModel.deadLine,

                    onCheckedChange = { checked ->
                        viewModel.onEvent(AddEditTareaEvent.OnDeadlineChange(checked))
                    },
                    modifier = Modifier.padding(end = 8.dp)
                )

                Text(
                    text = "¿Poner fecha límite?",
                    style = MaterialTheme.typography.h2,
                    modifier = Modifier.padding(15.dp)
                )

                IconButton(
                    onClick = { viewModel.isTooltipVisible = !viewModel.isTooltipVisible }
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Ayuda",
                        tint = Color.LightGray
                    )
                }
            }
            if (viewModel.isTooltipVisible) {
                // Cuadro de texto emergente
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .background(Color.LightGray)
                        .border(1.dp, Color.White)
                        .clickable { viewModel.isTooltipVisible = false }
                        .pointerInput(Unit) {
                            detectTapGestures { viewModel.isTooltipVisible = false }
                        }
                ) {
                    Text(
                        text = "La tarea será Fallida si no se completa antes de la fecha y hora señalada",
                        style = MaterialTheme.typography.button,
                        modifier = Modifier.padding(15.dp)
                    )
                }
            }
            //PickImageFromGallery()
        }
    }
}

@Composable
fun ConfirmationButtons(viewModel: ViewModelAddEditTarea) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        FloatingActionButton(
            onClick = {
                viewModel.onEvent(AddEditTareaEvent.OnCloseTareaClick)
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
                viewModel.onEvent(AddEditTareaEvent.OnSaveTareaClick)
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

@Composable
fun Formulario(viewModel: ViewModelAddEditTarea)  {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Creación de tarea",
            style = MaterialTheme.typography.h1,
            modifier = Modifier.padding(15.dp)
        )
        TextField(
            value = viewModel.title,
            onValueChange = {
                viewModel.onEvent(AddEditTareaEvent.OnTitleChange(it))
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
                viewModel.onEvent(AddEditTareaEvent.OnDescriptionChange(it))
            },
            placeholder = {
                Text(text = "Descripción")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = false,
            maxLines = 5
        )

        Spacer(modifier = Modifier.size(16.dp))
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun PickImageFromGallery() {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri
        }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(size = 400.dp))
            .padding(20.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        imageUri?.let { uri ->
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            bitmap.value = ImageDecoder.decodeBitmap(source)

            bitmap.value?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(400.dp)
                        .padding(20.dp)
                        .clip(CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = { launcher.launch("image/*") }) {
            Text(text = "Escoger imagen")
        }
    }
}
