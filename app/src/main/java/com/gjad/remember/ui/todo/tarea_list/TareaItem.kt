package com.gjad.remember.ui.todo.tarea_list

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gjad.remember.R
import com.gjad.remember.data.Tarea
import com.gjad.remember.ui.reminder.recordatorio_list.RecordatorioListEvent
import com.gjad.remember.ui.theme.*
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TareaItem(
    tarea: Tarea,
    onEvent: (TareaListEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedState by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 180f else 0f
    )
    val swipeDisabled = tarea.progress
    val win = SwipeAction(
    icon = {

        Text(
            text = "Completado",
            style = MaterialTheme.typography.h2,
            color = DeepBlue
        )
        Icon(
            modifier = Modifier.padding(16.dp),
            painter = painterResource(id = R.drawable.ic_military),
            contentDescription = "Completado",
            tint = DeepBlue
        )
           },
        background = if (swipeDisabled == 1) Color.Gray else Color.Green,
    onSwipe = {
        if (swipeDisabled != 1) {
            onEvent(TareaListEvent.OnProgressChange(tarea, 1, "¡Enhorabuena!"))
        }
        //la tarea se convierte en Done, progress = 1, desaparece de la lista
    }
)

    val fail = SwipeAction(
        icon = {
            Icon(
                modifier = Modifier.padding(16.dp),
                painter = painterResource(id = R.drawable.ic_sad),
                contentDescription = "Fallado",
                tint = TextWhite
            )
            Text(
                text = "Fallado",
                style = MaterialTheme.typography.h2,
                color = TextWhite
            )
        },
        background = if (swipeDisabled == 2) Color.Gray else Color.Red,
        isUndo = true,
        onSwipe = {
            if (swipeDisabled != 2) {
                onEvent(TareaListEvent.OnProgressChange(tarea, 2, "Lo conseguirás la próxima vez"))
            }
            //la tarea se convierte en Fail, progress = 2, desaparece de la lista
        },
    )

    SwipeableActionsBox(
        startActions = listOf(win),
        endActions = listOf(fail),
        swipeThreshold = 100.dp,
        backgroundUntilSwipeThreshold = DeepBlue
    ) {
        Card(
            shape = RoundedCornerShape(10.dp),
            backgroundColor = ButtonBlue,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .animateContentSize(
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = LinearOutSlowInEasing
                    )
                ),
            onClick = {
                expandedState = !expandedState
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = tarea.title,
                        style = MaterialTheme.typography.h2,
                        lineHeight = 26.sp,
                        modifier = Modifier
                            //.align(Alignment.TopStart)
                            .weight(6f),
                    )
                    IconButton(
                        modifier = Modifier
                            .weight(1f)
                            .alpha(ContentAlpha.medium)
                            .rotate(rotationState),
                        onClick = {
                            expandedState = !expandedState
                        }) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Drop-Down Arrow",
                            modifier = Modifier.size(35.dp),
                            tint = if (expandedState) TextWhite else DeepBlue
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    tarea.formattedTime?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.h2,
                            lineHeight = 5.sp,
                            modifier = Modifier
                                //.align(Alignment.TopStart)
                                .weight(6f),
                        )
                        tarea.formattedDate?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.h2,
                                lineHeight = 5.sp,
                                modifier = Modifier
                                    //.align(Alignment.TopStart)
                                    .weight(6f),
                            )
                        }
                    }
                }
                if (expandedState) {
                    tarea.description?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.body2,
                            color = TextWhite,
                            modifier = Modifier
                                //.align(Alignment.TopStart)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        IconButton(onClick = {
                            onEvent(
                                TareaListEvent.OnTareaClick(tarea)
                            )
                        }) {
                            Icon(
                                imageVector = Icons.Default.Create,
                                contentDescription = "Editar",
                                tint = TextWhite
                            )
                        }
                        Text(
                            text = "Fecha creación: "+tarea.creationDate,
                            style = MaterialTheme.typography.body1
                        )
                        IconButton(onClick = {
                            onEvent(
                                TareaListEvent.OnDeleteTareaClick(
                                    tarea,
                                    "Tarea borrada"
                                )
                            )
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Borrar",
                                tint = TextWhite
                            )
                        }

                    }

                }
            }
        }
    }
}