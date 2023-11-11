package com.gjad.remember.ui.tarjeta

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

data class Tarjeta(
    val title: String,
    @DrawableRes val iconId: Int,
    val lightColor: Color,
    val mediumColor: Color,
    val darkColor: Color
)
