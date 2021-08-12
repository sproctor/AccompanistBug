package com.example.accompanistbug

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.example.accompanistbug.ui.theme.AccompanistBugTheme
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.imePadding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private var view: View? = null

    @ExperimentalMaterialApi
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            view = LocalView.current
            AccompanistBugTheme {
                ProvideWindowInsets {
                    NavHost(navController = navController, startDestination = "test1") {
                        composable("test1") {
                            ScreenWithout(navController = navController)
                        }
                        composable("test2") {
                            ScreenWithKeyboard(navController = navController)
                        }
                    }
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            view?.let { view ->
                val controller = WindowInsetsControllerCompat(window, view)
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun ScreenWithout(navController: NavController) {
    ScaffoldWrapper(title = "test1", navController = navController) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Red,
            onClick = {
                navigate(navController = navController, route = "test2")
            }
        ) {
            Text("first screen")
        }
    }
}

@Composable
fun ScaffoldWrapper(title: String, navController: NavController, content: @Composable () -> Unit) {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        modifier = Modifier.imePadding(),
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(onClick = { /*TODO*/ }) {
                Text("+")
            }
        },
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                scaffoldState.drawerState.open()
                            }
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        drawerContent = {
            Column {
                TextButton(
                    onClick = {
                        scope.launch {
                            scaffoldState.drawerState.close()
                        }
                        navigate(navController = navController, route = "test1")
                    }
                ) {
                    Text("test1")
                }
                TextButton(
                    onClick = {
                        scope.launch {
                            scaffoldState.drawerState.close()
                        }
                        navigate(navController = navController, route = "test2")
                    }
                ) {
                    Text("test2")
                }
            }
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            content()
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun ScreenWithKeyboard(navController: NavController) {
    ScaffoldWrapper(title = "test2", navController = navController) {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Green,
            onClick = {
                navigate(navController = navController, route = "test1")
            }
        ) {
            Column {
                val focusRequester = remember { FocusRequester() }
                TextField(
                    modifier = Modifier.focusRequester(focusRequester),
                    value = "example text",
                    onValueChange = {})
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                    focusRequester.captureFocus()
                }
            }
        }
    }
}

fun navigate(navController: NavController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.startDestinationId) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}