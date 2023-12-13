package com.example.memominder

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.memominder.ui.theme.FontTittle
import java.text.SimpleDateFormat
import java.util.Date

//This function does a Scaffold, and this calls to the navigation bar and to the function of the DatePickerView.
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
            .padding(bottom = it.calculateBottomPadding())
            .background(backgroundBrush)) {
            DatePickerView(navController)
        }

    }
}

//This function create the datePicker, that creates the calendar, and apart, prints the activities of each date at the bottom of the calendar, when you choose a date.
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
            state = datePickerState,
            dateFormatter = DatePickerDefaults.dateFormatter(),
            showModeToggle = true,
            colors = DatePickerDefaults.colors(dividerColor = Color.Transparent),
            headline = { Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                if (selectedDate == null) {
                    Text(text = "No date selected", modifier = Modifier.padding(start = 10.dp))
                } else {
                    Text(text = printDate(selectedDate.toString()), fontFamily = FontTittle)
                }
            }}
        )

        var dayActivities by remember {
            mutableStateOf("")
        }

        var currentDate by remember {
            mutableStateOf("")
        }

        var message by remember { mutableStateOf("") }

        if (selectedDate == null) {
            Text(text = "No date selected", modifier = Modifier.padding(start = 10.dp))
        } else {
            Text(text = printDate(selectedDate.toString()), fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.size(20.dp))

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

        if (dayActivities != "" && currentDate == selectedDate.toString()) {
            listDay(date = selectedDate.toString(), activities = dayActivities)
        }

        Button(onClick = {
            navController.navigate("Day/${selectedDate.toString()}")
        }) {
            Text(text = "Add new activity", fontFamily = FontTittle)
        }

        Text(text = message)
    }
}

//This function formats the date from milliseconds to a String.
@SuppressLint("SimpleDateFormat")
fun formatedDate(millis: Long): String {
    val date = Date(millis)
    val day = SimpleDateFormat("dd").format(date)
    val month = SimpleDateFormat("MM").format(date)
    val year = SimpleDateFormat("yyyy").format(date)

    return "$year-$month-$day"
}

//This function formats the date from the date of the database to the date that is printed in the calendar.
fun printDate(date: String) : String {
    val separatedDate = separateDate(date)
    var monthLetters = ""
    monthLetters = monthLetters(separatedDate[1])

    return "$monthLetters ${separatedDate[2]}, ${separatedDate[0]}"
}

//This function changes a month with numbers to a month with letters
fun monthLetters(month : Int) : String {
    when (month) {
        1 -> return "January"
        2 -> return "February"
        3 -> return "March"
        4 -> return "April"
        5 -> return "May"
        6 -> return "June"
        7 -> return "July"
        8 -> return "August"
        9 -> return "September"
        10 -> return "October"
        11 -> return "November"
        else -> return "December"
    }
}

//This function separates a date that is in a String to a List with the day, month and year.
fun separateDate(date : String) : List<Int> {
    val separated = date.split("-")
    val day = separated[2].toInt()
    val month = separated[1].toInt()
    val year = separated[0].toInt()
    return listOf(year, month, day)
}

//And this function merges a date that is separated.
fun mergeDate(list : List<String>) : String {
    return "${list[0]}-${list[1]}-${list[2]}"
}