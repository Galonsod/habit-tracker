package com.gjad.remember.util

sealed class UIEvent {
    object PopBackStack : UIEvent()
    data class Navigate(val ruta: String) : UIEvent()
    data class ShowSnackbar(
        val mensaje: String,
        val accion: String? = null
    ) : UIEvent()
}