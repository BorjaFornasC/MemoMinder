package com.example.memominder

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.time.Month

@Composable
fun diary(navController : NavHostController) {
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
            printDiary()
        }

    }
}

var activities = mutableListOf<DayActivities>()
fun allActivities(context: Context) {
    val url = "https://memominder.000webhostapp.com/listararticulos.php"
    val requestQueue = Volley.newRequestQueue(context)
    val jsonObjectRequest = JsonObjectRequest(
        Request.Method.GET,
        url,
        null,
        { response ->
            val jsonArray = response.getJSONArray("lista")
            activities.clear()
            for (i in 0 until jsonArray.length()) {
                val registro = jsonArray.getJSONObject(i)
                val date = registro.getString("fecha")
                val activitiesToday = registro.getString("actividades")
                activities.add(DayActivities(date, activitiesToday))
            }
        },
        { error ->
        }
    )
    requestQueue.add(jsonObjectRequest)
}

data class DaysSeparatedDates(val day : String, val month: String, val year: String, val activities : String)
@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("ResourceType", "SimpleDateFormat")
@Composable
fun printDiary() {
    val dates = mutableListOf<DaysSeparatedDates>()
    for (day in activities) {
        val daySep = separateDate(day.date)
        dates.add(DaysSeparatedDates(daySep[0], daySep[1], daySep[2], day.activities))
    }
    val months : Map<String, List<DaysSeparatedDates>> = dates.groupBy { it.month }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        months.forEach { (month, myDate) ->
            stickyHeader {
                Text(
                    text = month,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.LightGray),
                    fontSize = 16.sp
                )
            }
            items(myDate) { today ->
                Card(
                    elevation = CardDefaults.cardElevation(5.dp),
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Date: ${mergeDate(listOf(today.day, today.month, today.year))}",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(start = 10.dp, end = 10.dp,
                                bottom = 5.dp)
                        )
                        Text(
                            text = "Activities: ${today.activities}",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }
    }
}