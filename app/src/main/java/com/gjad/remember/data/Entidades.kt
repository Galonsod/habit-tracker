package com.gjad.remember.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Recordatorio(

    val formattedDate: String,      //requerido para crear Recordatorio, se inicializa como LocalDate.now() formateado
    val formattedTime: String,     //si es nulo o blank, no se genera alarma
    val title: String,              //requerido para crear Recordatorio
    val description: String,
    @PrimaryKey val id: Int? = null //se autogenera, nunca sera null
)

@Entity
data class Tarea(

    val formattedDate: String,     //se inicializa como LocalDate.now() formateado
    val formattedTime: String,     //si es nulo o blank, no se genera alarma
    val creationDate: String,
    //val category: String?,        //atributo para filtrar Tareas
    val title: String,              //requerido para crear Tareas
    val description: String,
    val progress: Int,          //Check de Tarea, a cambiar por fail/success para filtrar Tareas. SE MUESTRA LA LISTA DE TAREAS CON PROGRESS = NULL EN LISTA
    //val periodico: ???            para crear la tarea constantemente en un patron temporal
    val deadLine: Boolean,     // indica que la tarea tiene un tiempo limite que sera formattedDate y formattedTime
    @PrimaryKey val id: Int? = null //se autogenera, nunca sera null
)

/*@Entity
data class Habito(

    val daysOfWeek: BooleanArray,
    //val category: String?,
    val creationDate: String,
    val title: String,
    val description: String?,
    //val periodico: ???
    @PrimaryKey val id: Int? = null
)*/

/*@Entity
data class Categoria(
    val title: String
    @PrimaryKey val id: Int? = null
)*/