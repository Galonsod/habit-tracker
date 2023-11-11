package com.gjad.remember.ui.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.gjad.remember.ui.bottom_menu.BottomMenu
import com.gjad.remember.ui.home.HomeTopBar
import com.gjad.remember.ui.reminder.recordatorio_list.ViewModelRecordatorioList
import com.gjad.remember.ui.sign_in.UserData
import com.gjad.remember.ui.theme.DeepBlue
import com.gjad.remember.ui.todo.tarea_list.TareaListEvent
import com.gjad.remember.ui.todo.tarea_list.ViewModelTareaList
import com.gjad.remember.util.UIEvent


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme

import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.gjad.remember.ui.theme.TextWhite

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ProfileScreen(
    onNavigate: (UIEvent.Navigate) -> Unit,
    userData: UserData?,
    onSignOut: () -> Unit,
    viewModelTarea: ViewModelTareaList = hiltViewModel(),
    viewModelRecordatorio: ViewModelRecordatorioList = hiltViewModel()
) {
    val scaffoldState = rememberScaffoldState()
    LaunchedEffect(key1 = true) {
        viewModelTarea.uiEvent.collect { event ->
            when (event) {
                is UIEvent.Navigate -> onNavigate(event)
                else -> Unit
            }
        }
    }
    LaunchedEffect(key1 = true) {
        viewModelRecordatorio.uiEvent.collect { event ->
            when (event) {
                is UIEvent.Navigate -> onNavigate(event)
                else -> Unit
            }
        }
    }
    Scaffold(
        backgroundColor = DeepBlue,
        scaffoldState = scaffoldState,
        bottomBar = { BottomMenu() },
        topBar = { ProfileTopBar() }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (userData?.profilePictureUrl != null) {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .border(width = 2.dp, color = TextWhite, shape = CircleShape)
                ) {
                    AsyncImage(
                        model = userData.profilePictureUrl,
                        contentDescription = "Profile picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (userData?.username != null) {
                Text(
                    text = userData.username,
                    textAlign = TextAlign.Center,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextWhite
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            Button(onClick = onSignOut) {
                Text(
                    text = "Cerrar sesión",
                    color = TextWhite
                )
            }
        }
    }
}

@Composable
fun ProfileTopBar() {
    Column(
        modifier = Modifier.padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Profile",
            style = MaterialTheme.typography.h1,
            modifier = Modifier.padding(bottom = 15.dp)
        )
    }
}
