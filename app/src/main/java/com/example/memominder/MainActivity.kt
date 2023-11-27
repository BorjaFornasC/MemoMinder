package com.example.memominder
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.memominder.ui.theme.MemoMinderTheme
import java.text.SimpleDateFormat
import java.util.Date

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
                        NavHost(navController = navController, startDestination = "Portada") {
                            composable("Portada") { portada(navController) }
                            composable("Calendar") { DatePickerView() }
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

    val items = listOf("Calendar", "CoffeeShops", "ElSol")

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItem == index,
                icon = {
                    when (item) {
                        "Calendar" -> Icon(Icons.Filled.AccountBox, contentDescription = item)
                        "CoffeeShops" -> Icon(Icons.Filled.Favorite, contentDescription = item)
                        else -> Icon(Icons.Filled.Face, contentDescription = item)
                    }
                },
                onClick = {
                    selectedItem = index
                    when (selectedItem) {
                        0 -> navHostController.navigate("Calendar")
                        1 -> navHostController.navigate("CoffeeShops")
                        2 -> navHostController.navigate("ElSol")
                    }
                },
                label = { Text(text = item) }
            )
        }
    }
}