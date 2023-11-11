package com.gjad.remember.ui.bottom_menu

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.vectorResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.gjad.remember.R

import com.gjad.remember.ui.reminder.recordatorio_list.RecordatorioListEvent
import com.gjad.remember.ui.reminder.recordatorio_list.ViewModelRecordatorioList
import com.gjad.remember.ui.theme.AquaBlue
import com.gjad.remember.ui.theme.ButtonBlue
import com.gjad.remember.ui.theme.DeepBlue
import com.gjad.remember.ui.todo.tarea_list.TareaListEvent

import com.gjad.remember.ui.todo.tarea_list.ViewModelTareaList
import com.gjad.remember.util.Rutas



@Composable
fun BottomMenu() {

    val isMenuExtended = remember { mutableStateOf(false) }
    val selected = remember { mutableStateOf(false) }

    //velocidad despliegue navbar2
    val fabAnimationProgress by animateFloatAsState(
        targetValue = if (isMenuExtended.value) 1f else 0f,
        animationSpec = tween(
            durationMillis = 850,
            easing = LinearEasing
        )
    )

    //velocidad animacion boton navbar1 seleccionado
    val clickAnimationProgress by animateFloatAsState(
        targetValue = if (isMenuExtended.value) 1f else 0f,
        animationSpec = tween(
            durationMillis = 600,
            easing = LinearEasing
        )
    )

    //evita crasheos en moviles con version de android < 12
    val renderEffect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getRenderEffect().asComposeRenderEffect()
    } else {
        null
    }

    BottomMenu(
        renderEffect = renderEffect,
        fabAnimationProgress = fabAnimationProgress,
        clickAnimationProgress = clickAnimationProgress
    ) {
        isMenuExtended.value = isMenuExtended.value.not()
    }
}

@Composable
fun BottomMenu(
    renderEffect: androidx.compose.ui.graphics.RenderEffect?,
    fabAnimationProgress: Float = 0f,
    clickAnimationProgress: Float = 0f,
    toggleAnimation: () -> Unit = { },


) {
    Box(
        Modifier
            .fillMaxSize(),
            //.padding(bottom = 24.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        CustomBottomNavigation(
            items = listOf(
                BottomMenuContent("Home", Rutas.HOME, R.drawable.ic_home),
                BottomMenuContent("To Do", Rutas.TODO_LIST, R.drawable.ic_todo),
                BottomMenuContent("", Rutas.HOME, R.drawable.ic_moon),
                BottomMenuContent("Remind", Rutas.REMINDER_LIST, R.drawable.ic_remind),
                BottomMenuContent("Profile", Rutas.PROFILE, R.drawable.ic_profile),
            ),
            modifier = Modifier.align(Alignment.BottomCenter)


        )

        FabGroup(renderEffect = renderEffect, animationProgress = fabAnimationProgress)
        FabGroup(
            renderEffect = null,
            animationProgress = fabAnimationProgress,
            toggleAnimation = toggleAnimation,

        )
        Circle(
            color = Color.White,
            animationProgress = clickAnimationProgress
        )
    }
}


@Composable
fun CustomBottomNavigation(
    items: List<BottomMenuContent>,
    modifier: Modifier = Modifier,
    activeHighlightColor: Color = ButtonBlue,
    activeTextColor: Color = Color.White,
    inactiveTextColor: Color = AquaBlue,
    initialSelectedItemIndex: Int = 3,
    viewModelTarea: ViewModelTareaList = hiltViewModel(),
    viewModelRecordatorio: ViewModelRecordatorioList = hiltViewModel()

    //navController: NavController // Agrega el parámetro del NavController

) {
    //var selectedItemIndex by remember { mutableStateOf(initialSelectedItemIndex) }
    //val navController = rememberNavController()
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(DeepBlue)
            .padding(15.dp)
            .scale(1.15f)

    ) {
        items.forEachIndexed { index, item ->
            BottomMenuItem(
                item = item,
                //isSelected = index == selectedItemIndex,
                activeHighlightColor = activeHighlightColor,
                activeTextColor = activeTextColor,
                inactiveTextColor = inactiveTextColor
            ) {
                //selectedItemIndex = index
                //navController.navigate(item.route)
                if (item.title == "Home") { // Comprueba si el ítem es "Home"

                   viewModelTarea.onEvent(TareaListEvent.OnHomeMenu) // Navega a la pantalla de inicio (Home)
                }
                if (item.title == "To Do") { // Comprueba si el ítem es "Home"
                    viewModelTarea.onEvent(TareaListEvent.OnTareaListMenu) // Navega a la pantalla de inicio (Home)
                }
                if (item.title == "Remind") { // Comprueba si el ítem es "Home"
                    viewModelRecordatorio.onEvent(RecordatorioListEvent.OnRecordatorioListMenu) // Navega a la pantalla de inicio (Home)
                }
                if (item.title == "Profile") { // Comprueba si el ítem es "Home"
                    viewModelRecordatorio.onEvent(RecordatorioListEvent.OnProfileMenu) // Navega a la pantalla de inicio (Home)
                }
                 // Navega a la pantalla de inicio (Home)

            }
        }
    }
}

@Composable
fun BottomMenuItem(
    item: BottomMenuContent,
    isSelected: Boolean = false,
    activeHighlightColor: Color = ButtonBlue,
    activeTextColor: Color = Color.White,
    inactiveTextColor: Color = AquaBlue,
    onItemClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.clickable(enabled = item.title.isNotEmpty()) {
            onItemClick()
        }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(if (isSelected) activeHighlightColor else Color.Transparent)
                .padding(10.dp)
        ) {
            Icon(
                painter = painterResource(id = item.iconId),
                contentDescription = item.title,
                tint = if (isSelected) activeTextColor else inactiveTextColor,
                modifier = Modifier.size(25.dp)
            )
        }
        Text(
            text = item.title,
            color = if(isSelected) activeTextColor else inactiveTextColor
        )
    }
}





@RequiresApi(Build.VERSION_CODES.S)
private fun getRenderEffect(): RenderEffect {
    val blurEffect = RenderEffect
        .createBlurEffect(80f, 80f, Shader.TileMode.MIRROR)

    val alphaMatrix = RenderEffect.createColorFilterEffect(
        ColorMatrixColorFilter(
            ColorMatrix(
                floatArrayOf(
                    1f, 0f, 0f, 0f, 0f,
                    0f, 1f, 0f, 0f, 0f,
                    0f, 0f, 1f, 0f, 0f,
                    0f, 0f, 0f, 50f, -5000f
                )
            )
        )
    )
    return RenderEffect
        .createChainEffect(alphaMatrix, blurEffect)
}

@Composable
fun Circle(
    color: Color,
    animationProgress: Float,

) {
    val animationValue = sin(PI * animationProgress).toFloat()

    Box(
        modifier = Modifier
            .padding(bottom = 30.dp)
            .size(56.dp)
            .scale(2 - animationValue)
            .border(
                width = 2.dp,
                color = color.copy(alpha = color.alpha * animationValue),
                shape = CircleShape
            )
    )
}

@Composable
fun FabGroup(
    animationProgress: Float = 0f,
    renderEffect: androidx.compose.ui.graphics.RenderEffect? = null,
    toggleAnimation: () -> Unit = { },
    viewModelTarea: ViewModelTareaList = hiltViewModel(),
    viewModelRecordatorio: ViewModelRecordatorioList = hiltViewModel(),

) {

    Box(
        Modifier
            .fillMaxSize()
            .graphicsLayer { this.renderEffect = renderEffect }
            .padding(bottom = 30.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedFab(
            icon = R.drawable.ic_addtask,
            modifier = Modifier
                .padding(
                    PaddingValues(
                        bottom = 88.dp,
                        end = 150.dp
                    ) * FastOutSlowInEasing.transform(0f, 0.8f, animationProgress)
                ),
            onClick = {
                viewModelTarea.onEvent(TareaListEvent.OnAddTareaClick) },
            opacity = LinearEasing.transform(0.2f, 0.7f, animationProgress)
        )

        AnimatedFab(
            icon = R.drawable.ic_addremind,
            modifier = Modifier.padding(
                PaddingValues(
                    bottom = 100.dp,
                ) * FastOutSlowInEasing.transform(0.1f, 0.9f, animationProgress)
            ),
            onClick = {
                viewModelRecordatorio.onEvent(RecordatorioListEvent.OnAddRecordatorioClick) },
            opacity = LinearEasing.transform(0.3f, 0.8f, animationProgress)
        )

        AnimatedFab(
            icon = R.drawable.ic_addlabel,
            modifier = Modifier.padding(
                PaddingValues(
                    bottom = 88.dp,
                    start = 150.dp
                ) * FastOutSlowInEasing.transform(0.2f, 1.0f, animationProgress)
            ),
            //onClick = { viewModel.onEvent(AddEditCategoryEvent.OnCloseCategoryClick) },
            opacity = LinearEasing.transform(0.4f, 0.9f, animationProgress)
        )

        AnimatedFab(
            icon = R.drawable.ic_monetization,
            modifier = Modifier
                .padding(
                    PaddingValues(
                        bottom = 628.dp,
                        start = 295.dp
                    ) * FastOutSlowInEasing.transform(0f, 0.8f, animationProgress)
                ),
            opacity = LinearEasing.transform(0.2f, 0.7f, animationProgress)
        )

        AnimatedFab(
            modifier = Modifier
                .scale(1f - LinearEasing.transform(0.5f, 0.85f, animationProgress)),
        )

        AnimatedFab(
            icon = R.drawable.ic_add,
            modifier = Modifier
                .rotate(
                    225 * FastOutSlowInEasing
                        .transform(0.35f, 0.65f, animationProgress)
                ),
            onClick = toggleAnimation,
            backgroundColor = Color.Transparent
        )
    }
}


 // Importa el ícono predeterminado que se utilizará si el recurso no está presente

@Composable
fun AnimatedFab(
    modifier: Modifier,
    @DrawableRes icon: Int? = null,
    opacity: Float = 1f,
    backgroundColor: Color = ButtonBlue,
    onClick: () -> Unit = {}
) {
    FloatingActionButton(
        onClick = onClick,
        elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp),
        backgroundColor = backgroundColor,
        modifier = modifier
            .scale(1.17f)
            .border(
                border = BorderStroke(0.8.dp, Color.White),
                shape = RoundedCornerShape(percent = 50)
            )
    ) {
        icon?.let {
            Icon(
                imageVector = ImageVector.vectorResource(icon),
                contentDescription = null,
                tint = Color.White.copy(alpha = opacity)
            )
        }
    }
}


