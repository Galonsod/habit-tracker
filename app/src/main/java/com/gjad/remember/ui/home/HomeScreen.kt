package com.gjad.remember.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.gjad.remember.ui.tarjeta.Tarjeta
import com.gjad.remember.ui.tarjeta.standardQuadFromTo
import com.gjad.remember.R
import com.gjad.remember.ui.bottom_menu.BottomMenu
import com.gjad.remember.ui.reminder.recordatorio_list.RecordatorioItem
import com.gjad.remember.ui.reminder.recordatorio_list.RecordatorioListEvent
import com.gjad.remember.ui.reminder.recordatorio_list.ViewModelRecordatorioList
import com.gjad.remember.ui.sign_in.UserData
import com.gjad.remember.ui.theme.*
import com.gjad.remember.ui.todo.tarea_list.TareaItem
import com.gjad.remember.ui.todo.tarea_list.TareaListEvent
import com.gjad.remember.ui.todo.tarea_list.ViewModelTareaList
import com.gjad.remember.util.UIEvent
import java.time.LocalTime



@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@ExperimentalFoundationApi
@Composable
fun HomeScreen(
    userData: UserData?,
    onNavigate: (UIEvent.Navigate) -> Unit,
    viewModelTarea: ViewModelTareaList = hiltViewModel(),
    viewModelRecordatorio: ViewModelRecordatorioList = hiltViewModel(),
) {
    val tareasToday = viewModelTarea.tareasToday.collectAsState(initial = emptyList())
    val recordatoriosToday = viewModelRecordatorio.recordatoriosToday.collectAsState(initial = emptyList())

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
        //contentColor = Color.White,
        scaffoldState = scaffoldState
    ) {
        Column {
            HomeTopBar(userData)
            NewsSection()
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
                            text = "Recordatorios para hoy",
                            style = MaterialTheme.typography.h1,
                            modifier = Modifier.padding(bottom = 15.dp)
                        )
                        if (recordatoriosToday.value.isEmpty()) {
                            Text(
                                text = "Hoy no queda nada por hacer.",
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
//val account = GoogleSignIn.getLastSignedInAccount(this)
//val name = account?.displayName ?: "" // "" es el valor por defecto si no se encuentra el nombre
//añadir al build gradle esto segun CHATGPT: implementation 'com.google.android.gms:play-services-auth:19.2.0'
fun GreetingSection(
    userData: UserData?
    //TODO cambiar lo de arriba por // name: String, recogido por la ruta nav desde login
) {
    val name = userData?.username ?: ""
    val time = LocalTime.now()

    //TODO ver si esto funciona bien
    val greeting = when (time.hour) {
        in 5..11 -> "Buenos días"
        in 12..16 -> "Buen mediodía"
        in 17..20 -> "Buenas tardes"
        else -> "Buenas noches"
    }
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$greeting $name,",
                style = MaterialTheme.typography.h2
            )
            Text(
                text = "¡Te deseamos un buen día!",
                style = MaterialTheme.typography.body1
            )
        }
        if (userData?.profilePictureUrl != null) {
            Box(
                modifier = Modifier
                    .size(35.dp)
                    .clip(CircleShape)
                    .border(width = 1.dp, color = TextWhite, shape = CircleShape)
            ) {
                AsyncImage(
                    model = userData?.profilePictureUrl,
                    contentDescription = "Profile picture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ChipSection(
    chips: List<String>
) {
    var selectedChipIndex by remember {
        mutableStateOf(0)
    }
    LazyRow {
        items(chips.size) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(start = 15.dp, top = 15.dp, bottom = 15.dp)
                    .clickable {
                        selectedChipIndex = it
                    }
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (selectedChipIndex == it) ButtonBlue
                        else DarkerButtonBlue
                    )
                    .padding(15.dp)
            ) {
                Text(text = chips[it], color = TextWhite)
            }
        }
    }
}

@Composable
fun NewsSection(
    color: Color = LightRed
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(15.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color)
            .padding(horizontal = 15.dp, vertical = 20.dp)
            .fillMaxWidth()
    ) {
        Column {
            Text(
                text = "¡Más actualizaciones en el futuro!",
                style = MaterialTheme.typography.h2
            )
            Text(
                text = "Esperamos que esta presentación os guste",
                style = MaterialTheme.typography.body1,
                color = TextWhite
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(width = 1.dp, color = TextWhite, shape = CircleShape)
                .background(ButtonBlue)
                .padding(10.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_thumb_up),
                contentDescription = "Play",
                tint = Color.White,
                modifier = Modifier.size(25.dp)
            )
        }
    }
}

//TARJETAITEM SERA RECORDATORIO Y SWIPEABLE TODO RESPECTIVAMENTE
@Composable
fun TarjetaItem(
    tarjeta: Tarjeta
) {
    BoxWithConstraints(
        modifier = Modifier
            .padding(7.5.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(tarjeta.darkColor)
    ) {
        val width = constraints.maxWidth
        val height = constraints.maxHeight

        // Medium colored path
        val mediumColoredPoint1 = Offset(0f, height * 0.3f)
        val mediumColoredPoint2 = Offset(width * 0.1f, height * 0.35f)
        val mediumColoredPoint3 = Offset(width * 0.4f, height * 0.05f)
        val mediumColoredPoint4 = Offset(width * 0.75f, height * 0.7f)
        val mediumColoredPoint5 = Offset(width * 1.4f, -height.toFloat())

        val mediumColoredPath = Path().apply {
            moveTo(mediumColoredPoint1.x, mediumColoredPoint1.y)
            standardQuadFromTo(mediumColoredPoint1, mediumColoredPoint2)
            standardQuadFromTo(mediumColoredPoint2, mediumColoredPoint3)
            standardQuadFromTo(mediumColoredPoint3, mediumColoredPoint4)
            standardQuadFromTo(mediumColoredPoint4, mediumColoredPoint5)
            lineTo(width.toFloat() + 100f, height.toFloat() + 100f)
            lineTo(-100f, height.toFloat() + 100f)
            close()
        }

        // Light colored path
        val lightPoint1 = Offset(0f, height * 0.35f)
        val lightPoint2 = Offset(width * 0.1f, height * 0.4f)
        val lightPoint3 = Offset(width * 0.3f, height * 0.35f)
        val lightPoint4 = Offset(width * 0.65f, height.toFloat())
        val lightPoint5 = Offset(width * 1.4f, -height.toFloat() / 3f)

        val lightColoredPath = Path().apply {
            moveTo(lightPoint1.x, lightPoint1.y)
            standardQuadFromTo(lightPoint1, lightPoint2)
            standardQuadFromTo(lightPoint2, lightPoint3)
            standardQuadFromTo(lightPoint3, lightPoint4)
            standardQuadFromTo(lightPoint4, lightPoint5)
            lineTo(width.toFloat() + 100f, height.toFloat() + 100f)
            lineTo(-100f, height.toFloat() + 100f)
            close()
        }
        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            drawPath(
                path = mediumColoredPath,
                color = tarjeta.mediumColor
            )
            drawPath(
                path = lightColoredPath,
                color = tarjeta.lightColor
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp)
        ) {
            Text(
                text = tarjeta.title,
                style = MaterialTheme.typography.h2,
                lineHeight = 26.sp,
                modifier = Modifier.align(Alignment.TopStart)
            )
            Icon(
                painter = painterResource(id = tarjeta.iconId),
                contentDescription = tarjeta.title,
                tint = Color.White,
                modifier = Modifier.align(Alignment.BottomStart)
            )
            Text(
                text = "Start",
                color = TextWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable {
                        // Handle the click
                    }
                    .align(Alignment.BottomEnd)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ButtonBlue)
                    .padding(vertical = 6.dp, horizontal = 15.dp)
            )
        }
    }
}


@Composable
fun HomeTopBar(userData: UserData?) {
    Column {
        GreetingSection(userData)

        // TODO: CHIPSECTION SERÁ LISTA DE CATEGORIAS QUE SE RECOGE DEL ROOM
        //ChipSection(chips = listOf("Health", "Work", "Social", "Literally me", "MFW", "AAAAAA"))
    }
}