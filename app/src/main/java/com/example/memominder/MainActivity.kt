package com.example.memominder
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.memominder.ui.theme.MemoMinderTheme

//This is the MainActivity of the app, and creates the navHost to navigate between the screens.
class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MemoMinderTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = it.calculateTopPadding())
                    ) {
                        val navController = rememberNavController()
                        NavHost(navController = navController, startDestination = DestinationScreen.SplashScreenDest.route) {
                            composable(route = DestinationScreen.SplashScreenDest.route) {
                                SplashScreen(navController = navController)
                            }
                            composable("FrontPage") { FrontPage(navController) }
                            composable("Calendar", enterTransition = {slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                                animationSpec = tween(700)
                            )}, exitTransition = {fadeOut(animationSpec = tween(500))}) { Calendar(navController = navController) }
                            composable("Day/{date}",
                                arguments = listOf(navArgument("date")
                                {type = NavType.StringType}))
                            {backStackEntry ->
                                day(backStackEntry.arguments?.getString("date") ?: "", navController)
                            }
                            composable("Diary", enterTransition = {slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                                animationSpec = tween(700)
                            )}, exitTransition = {fadeOut(animationSpec = tween(500))}) { diary(navController = navController)}

                        }
                    }
                }
            }
        }
    }
}
