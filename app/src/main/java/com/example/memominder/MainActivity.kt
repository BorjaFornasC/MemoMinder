package com.example.memominder
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.memominder.ui.theme.LightBlue
import com.example.memominder.ui.theme.FontTittle
import com.example.memominder.ui.theme.GrayLight
import com.example.memominder.ui.theme.MemoMinderTheme
import com.example.memominder.ui.theme.VibrantBlue
import com.example.memominder.ui.theme.VibrantYellow
import kotlinx.coroutines.delay

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

@Composable
fun MyNavigationBar(navHostController: NavHostController) {
    var selectedItem by remember {
        mutableIntStateOf(3)
    }

    val items = listOf("Calendar", "Diary")

    NavigationBar(containerColor = LightBlue) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItem == index,
                icon = {
                    when (item) {
                        "Calendar" -> Image(painter = painterResource(id = R.drawable.iconcalendar), contentDescription = null, modifier = Modifier.size(75.dp))
                        else -> Image(painter = painterResource(id = R.drawable.icondiary), contentDescription = null, modifier = Modifier.size(75.dp))
                    }
                },
                onClick = {
                    selectedItem = index
                    when (selectedItem) {
                        0 -> navHostController.navigate("Calendar")
                        1 -> navHostController.navigate("Diary")
                    }
                }
            )
        }
    }
}

sealed class DestinationScreen(val route: String) {
    object SplashScreenDest : DestinationScreen(route =
    "splash_screen")
    object MainScreenDest : DestinationScreen(route = "FrontPage")
}

@Composable
fun AnimationSplashContent(
    scaleAnimation: Animatable<Float, AnimationVector1D>,
    navController: NavController,
    durationMillisAnimation: Int,
    delayScreen: Long
) {

    LaunchedEffect(key1 = true) {
        scaleAnimation.animateTo(
            targetValue = 0.5F,
            animationSpec = tween(
                durationMillis = durationMillisAnimation,
                easing = {
                    OvershootInterpolator(3F).getInterpolation(it)
                }
            )
        )

        delay(timeMillis = delayScreen)

        navController.navigate(route =
        DestinationScreen.MainScreenDest.route) {
            popUpTo(route =
            DestinationScreen.SplashScreenDest.route) {
                inclusive = true
            }
        }
    }
}

val brushSplash = Brush.verticalGradient(
    colors = listOf(
        VibrantBlue,
        GrayLight,
        VibrantYellow
    )
)

@Composable
fun DesignSplashScreen(
    modifier: Modifier = Modifier,
    imagePainter: Painter,
    scaleAnimation: Animatable<Float, AnimationVector1D>
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brushSplash
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = imagePainter,
                contentDescription = "Logotipo Splash Screen",
                modifier = modifier
                    .size(500.dp)
                    .scale(scale = scaleAnimation.value)
            )

            Text(
                text = "Remember everywhere",
                color = Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontTittle,
                textAlign = TextAlign.Center,
                modifier = modifier.scale(scale =
                scaleAnimation.value)
            )
        }
    }
}

@Composable
fun SplashScreen(navController: NavController) {

    val scaleAnimation: Animatable<Float, AnimationVector1D> =
        remember { Animatable(initialValue = 0f) }

    AnimationSplashContent(
        scaleAnimation = scaleAnimation,
        navController = navController,
        durationMillisAnimation = 1500,
        delayScreen = 1500L
    )

    DesignSplashScreen(
        imagePainter = painterResource(id =
        R.drawable.logoblack),
        scaleAnimation = scaleAnimation
    )
}