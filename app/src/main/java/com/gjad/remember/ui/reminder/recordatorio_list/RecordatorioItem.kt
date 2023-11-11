package com.gjad.remember.ui.reminder.recordatorio_list

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gjad.remember.data.Recordatorio
import com.gjad.remember.ui.theme.AquaBlue
import com.gjad.remember.ui.theme.ButtonBlue
import com.gjad.remember.ui.theme.DeepBlue
import com.gjad.remember.ui.theme.TextWhite
import com.gjad.remember.ui.todo.tarea_list.TareaListEvent
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@Composable
fun a(
    recordatorio: Recordatorio,
    onEvent: (RecordatorioListEvent) -> Unit,
    modifier: Modifier = Modifier
    //color: color = color
) {
    val paddingModifier = Modifier.padding(10.dp)
    Card(
        shape = RoundedCornerShape(40.dp),
        elevation = 10.dp,
        modifier = paddingModifier,
        backgroundColor = AquaBlue
    ) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = recordatorio.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = recordatorio.formattedDate,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Light
                    )

                    Spacer(modifier = Modifier.width(8.dp))


                    //AQUI ICON BUTTON
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    recordatorio.description?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = it)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    recordatorio.formattedTime?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = it) //format adecuado aqui TODO
                    }
                }
            }

            //ICON BUTON ORIGINALMENTE BAJO SPACER SEÑALADO
            IconButton(onClick = {
                onEvent(
                    RecordatorioListEvent.OnDeleteRecordatorioClick(
                        recordatorio,
                        "Recordatorio borrado"
                    )
                )
            }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Borrar"
                )
            }
        }
    }
}




@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RecordatorioItem(
    recordatorio: Recordatorio,
    onEvent: (RecordatorioListEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expandedState by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 180f else 0f
    )

    Card(
        shape = RoundedCornerShape(40.dp),
        elevation = 10.dp,
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
                    text = recordatorio.title,
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
                Text(
                    text = recordatorio.formattedTime,
                    style = MaterialTheme.typography.h2,
                    lineHeight = 26.sp,
                    modifier = Modifier
                        //.align(Alignment.TopStart)
                        .weight(6f),
                )
                Text(
                    text = recordatorio.formattedDate,
                    style = MaterialTheme.typography.h2,
                    lineHeight = 26.sp,
                    modifier = Modifier
                        //.align(Alignment.TopStart)
                        .weight(6f),
                )
            }
            if (expandedState) {
                recordatorio.description?.let {
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
                            RecordatorioListEvent.OnRecordatorioClick(recordatorio)
                        )
                    }) {
                        Icon(
                            imageVector = Icons.Default.Create,
                            contentDescription = "Editar",
                            tint = TextWhite
                        )
                    }

                    IconButton(onClick = {
                        onEvent(
                            RecordatorioListEvent.OnDeleteRecordatorioClick(
                                recordatorio,
                                "Recordatorio borrado"
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

