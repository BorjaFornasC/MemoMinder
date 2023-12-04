package com.example.memominder

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import java.text.SimpleDateFormat
import java.util.Date

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Calendar(navController: NavHostController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            MyNavigationBar(navHostController = navController)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) {

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(bottom = it.calculateBottomPadding())) {
            DatePickerView(navController)
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerView(navController: NavHostController) {

    val context = LocalContext.current
    val currentSelectedDateMillis by remember {
        mutableStateOf(System.currentTimeMillis())
    }
    val datePickerState = rememberDatePickerState(currentSelectedDateMillis,selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            return true
        }
    })
    val selectedDate = datePickerState.selectedDateMillis?.let {
        formatedDate(it)
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.verticalScroll(
        rememberScrollState())) {

        DatePicker(
            state = datePickerState
        )
        Spacer(
            modifier = Modifier.height(
                5.dp
            )
        )
        var dayActivities by remember {
            mutableStateOf("")
        }
        var currentDate by remember {
            mutableStateOf("")
        }
        var message by remember { mutableStateOf("") }

        Text(text = printDate(selectedDate.toString()))
        
        Spacer(modifier = Modifier.size(20.dp))
        
        Button(onClick = {
            ConsultDate(
                date = selectedDate.toString(),
                response = {
                    if (it!=null) {
                        dayActivities = it.activities
                        currentDate = selectedDate.toString()
                        message = ""
                    } else {
                        message = "There aren't activities"
                    }
                }, context
            )

        }) {
            Text(text = "Consult")
        }

        if (dayActivities != "" && currentDate == selectedDate.toString()) {
            listDay(date = selectedDate.toString(), activities = dayActivities)
        }

        Button(onClick = {
            navController.navigate("Day/${selectedDate.toString()}")
        }) {
            Text(text = "Add new activity")
        }

        Text(text = message)
    }
}

@SuppressLint("SimpleDateFormat")
fun formatedDate(millis: Long): String {
    val date = Date(millis)
    val day = SimpleDateFormat("d").format(date)
    val month = SimpleDateFormat("MM").format(date)
    val year = SimpleDateFormat("yyyy").format(date)

    return "$year-$month-$day"
}

fun printDate(date: String) : String {
    val separatedDate = separateDate(date)
    var monthLetters = ""
    when (separatedDate[1]) {
        "01" -> monthLetters = "January"
        "02" -> monthLetters = "February"
        "03" -> monthLetters = "March"
        "04" -> monthLetters = "April"
        "05" -> monthLetters = "May"
        "06" -> monthLetters = "June"
        "07" -> monthLetters = "July"
        "08" -> monthLetters = "August"
        "09" -> monthLetters = "September"
        "10" -> monthLetters = "October"
        "11" -> monthLetters = "November"
        "12" -> monthLetters = "December"
    }

    return "$monthLetters ${separatedDate[2]}, ${separatedDate[0]}"
}

fun separateDate(date : String) : List<String> {
    val separated = date.split("-")
    val day = separated[2]
    val month = separated[1]
    val year = separated[0]
    return listOf(year, month, day)
}

fun mergeDate(list : List<String>) : String {
    return "${list[1]} ${list[0]}, ${list[2]}"
}