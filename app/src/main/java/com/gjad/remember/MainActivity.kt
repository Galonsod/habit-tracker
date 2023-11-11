package com.gjad.remember

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gjad.remember.ui.reminder.add_edit_recordatorio.AddEditRecordatorioScreen
import com.gjad.remember.ui.todo.add_edit_tarea.AddEditTareaScreen
import com.gjad.remember.ui.home.HomeScreen
import com.gjad.remember.ui.profile.ProfileScreen
import com.gjad.remember.ui.reminder.recordatorio_list.RecordatorioListScreen
import com.gjad.remember.ui.sign_in.SignInScreen
import com.gjad.remember.ui.sign_in.ViewModelSignIn
import com.gjad.remember.ui.todo.tarea_list.TareaListScreen
import com.gjad.remember.ui.theme.RememberTheme



import com.gjad.remember.util.Rutas
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch





import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue

import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.identity.Identity
import com.gjad.remember.ui.sign_in.GoogleAuthUIClient

import kotlinx.coroutines.launch
import kotlin.math.sign




@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val googleAuthUiClient by lazy {
        GoogleAuthUIClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    //@RequiresApi(Build.VERSION_CODES.P)
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RememberTheme() {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = Rutas.SIGN_IN) {
                        composable(Rutas.SIGN_IN) {
                            val viewModel = viewModel<ViewModelSignIn>()
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            LaunchedEffect(key1 = Unit) {
                                if(googleAuthUiClient.getSignedInUser() != null) {
                                    navController.navigate(Rutas.HOME)
                                }
                            }

                            val launcher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.StartIntentSenderForResult(),
                                onResult = { result ->
                                    if(result.resultCode == RESULT_OK) {
                                        lifecycleScope.launch {
                                            val signInResult = googleAuthUiClient.signInWithIntent(
                                                intent = result.data ?: return@launch
                                            )
                                            viewModel.onSignInResult(signInResult)
                                        }
                                    }
                                }
                            )

                            LaunchedEffect(key1 = state.isSignInSuccessful) {
                                if(state.isSignInSuccessful) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Inicio de sesión exitoso",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    navController.navigate(Rutas.HOME)
                                    viewModel.resetState()
                                }
                            }

                            SignInScreen(
                                state = state,
                                onSignInClick = {
                                    lifecycleScope.launch {
                                        val signInIntentSender = googleAuthUiClient.signIn()
                                        launcher.launch(
                                            IntentSenderRequest.Builder(
                                                signInIntentSender ?: return@launch
                                            ).build()
                                        )
                                    }
                                }
                            )
                        }
                        composable(Rutas.PROFILE) {
                            ProfileScreen(
                                onNavigate = {
                                    navController.navigate(it.ruta)
                                },
                                userData = googleAuthUiClient.getSignedInUser(),
                                onSignOut = {
                                    lifecycleScope.launch {
                                        googleAuthUiClient.signOut()
                                        Toast.makeText(
                                            applicationContext,
                                            "Cierre de sesión",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        navController.navigate(Rutas.SIGN_IN)
                                        //navController.popBackStack()
                                    }
                                }
                            )
                        }
                        composable(Rutas.HOME) {
                            HomeScreen(
                                userData = googleAuthUiClient.getSignedInUser(),
                                onNavigate = {
                                    navController.navigate(it.ruta)
                                }
                            )
                        }
                        composable(Rutas.TODO_LIST) {
                            TareaListScreen(
                                onNavigate = {
                                    navController.navigate(it.ruta)
                                }
                            )
                        }
                        composable(Rutas.REMINDER_LIST) {
                            RecordatorioListScreen(
                                onNavigate = {
                                    navController.navigate(it.ruta)
                                }
                            )
                        }
                        composable(
                            route = Rutas.ADD_EDIT_TODO + "?tareaId={tareaId}",
                            arguments = listOf(
                                navArgument(name = "tareaId") {
                                    type = NavType.IntType
                                    defaultValue = -1
                                }
                            )
                        ) {
                            AddEditTareaScreen(onPopBackStack = {
                                navController.popBackStack()
                            })
                        }
                        composable(
                            route = Rutas.ADD_EDIT_REMINDER + "?recordatorioId={recordatorioId}",
                            arguments = listOf(
                                navArgument(name = "recordatorioId") {
                                    type = NavType.IntType
                                    defaultValue = -1
                                }
                            )
                        ) {
                            AddEditRecordatorioScreen(onPopBackStack = {
                                navController.popBackStack()
                            })
                        }
                    }
                }
            }
        }
    }
}

/*
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RememberTheme {
                // A surface container using the 'background' color from the theme
                /*Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    ReminderCreation(ViewModelReminderCreation())*/

                    Navigation()
            }
        }
    }
}
*/
