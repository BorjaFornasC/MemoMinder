package com.example.memominder

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
            val context = LocalContext.current
            allActivities(context)
            if (dayActivities.isNotEmpty()) {
                printDiary()
            }
        }
    }
}
data class Actividad(val date: String, val activities: String)
val dayActivities = mutableStateListOf<Actividad>()
fun allActivities(context: Context) {
    val url = "https://memominder.000webhostapp.com/calendario/actividadesDiario.php"
    val requestQueue = Volley.newRequestQueue(context)
    val jsonObjectRequest = JsonObjectRequest(
        Request.Method.GET,
        url,
        null,
        { response ->
            val jsonArray = response.getJSONArray("lista")
            dayActivities.clear()
            for (i in 0 until jsonArray.length()) {
                val registro = jsonArray.getJSONObject(i)
                val codigo = registro.getString("fecha")
                val descripcion = registro.getString("actividades")
                dayActivities.add(Actividad(codigo, descripcion))
            }
        },
        { error ->
        }
    )
    requestQueue.add(jsonObjectRequest)
}

data class DaysSeparatedDates(
    val year: Int,
    val month: String,
    val monthNumber: Int,
    val day: Int,
    val activities: List<String>
)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun printDiary() {
    val dates = mutableListOf<DaysSeparatedDates>()
    for (day in dayActivities) {
        val daySep = separateDate(day.date)
        val activitiesNow = day.activities.split("&&")
        val datesSeparated = DaysSeparatedDates(daySep[0], monthLetters(daySep[1]), daySep[1], daySep[2], activitiesNow)
        dates.add(datesSeparated)
    }

    val years: Map<Int, Map<Int, List<DaysSeparatedDates>>> = dates
        .groupBy { it.year }
        .mapValues { (_, yearDates) ->
            yearDates.groupBy { it.monthNumber }
        }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        years.forEach { (year, months) ->
            stickyHeader {
                Text(
                    text = year.toString(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.LightGray),
                    fontSize = 16.sp
                )
            }
            months.forEach { (month, myDates) ->
                stickyHeader {
                    Text(
                        text = monthLetters(month),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray),
                        fontSize = 16.sp
                    )
                }
                items(myDates) { today ->
                    Card(
                        elevation = CardDefaults.cardElevation(5.dp),
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "Date: ${mergeDate(listOf(today.day.toString(), today.month, today.year.toString()))}",
                                fontSize = 18.sp,
                                modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 5.dp)
                            )
                            for (tAct in today.activities) {
                                Text(
                                    text = "Activity: ${tAct}",
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }

                        }
                    }
                }
            }
        }
    }
}