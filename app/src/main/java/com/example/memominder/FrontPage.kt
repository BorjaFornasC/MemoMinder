package com.example.memominder

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.memominder.ui.theme.GrayLight
import com.example.memominder.ui.theme.LightBlue

//This function does the Scaffold that calls to the NavigationBar to create the form to navigate between the screens, and calls to the function that returns the content of the front page.
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FrontPage(navController: NavHostController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            MyNavigationBar(navHostController = navController)
        },
        containerColor = GrayLight
    ) {

        Box(modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush), contentAlignment = Alignment.Center) {
            Welcome()
        }
    }
}

//This function returns the content of the Front Page.
@Composable
fun Welcome() {
    Box(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState()), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = R.drawable.logoblack),
            contentDescription = "Logotipo Splash Screen",
            modifier = Modifier
                .size(500.dp),
            contentScale = ContentScale.Fit
        )
    }
}

//And this function creates the navigation bar with the main screens.
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