package com.example.memominder

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memominder.ui.theme.LightBlue

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
            .padding(bottom = it.calculateBottomPadding())
            .background(brushSplash)) {
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
@Composable
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
                val date = registro.getString("fecha")
                val activities = registro.getString("actividades")
                dayActivities.add(Actividad(date, activities))
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
    var catena = ""
    val context = LocalContext.current
    for (day in dayActivities) {
        catena += "$day + \n"
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
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = year.toString(),
                        modifier = Modifier
                            .fillMaxWidth(),
                        fontSize = 50.sp,
                        textAlign = Center,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            months.forEach { (month, myDates) ->
                stickyHeader {
                    Text(
                        text = monthLetters(month),
                        modifier = Modifier
                            .fillMaxWidth().padding(start = 15.dp),
                        fontSize = 25.sp
                    )
                }
                
                items(myDates) { today ->
                    var todayActivities = mutableListOf<String>()
                    for (act in today.activities) {
                        todayActivities.add(act)
                    }

                    for (tAct in todayActivities) {

                        var showMenu by remember { mutableStateOf(false) }
                        val date = mergeDate(listOf(today.year.toString(), today.monthNumber.toString(), today.day.toString()))
                        var currentActivities by remember {
                            mutableStateOf("")
                        }
                        Row(modifier = Modifier.clickable{showMenu = !showMenu}) {

                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false },
                                Modifier.width(150.dp).background(LightBlue)
                            ) {
                                DropdownMenuItem(
                                    text = { Text(text = "Delete activity", color = Color.Black) },
                                    onClick = {
                                        if (todayActivities.size == 1) {
                                            Delete(date, response = {}, context = context)
                                            todayActivities.remove(tAct)
                                            showMenu = !showMenu
                                        } else {
                                            currentActivities = ""
                                            todayActivities.remove(tAct)
                                            for (a in todayActivities) {
                                                if (a != tAct) {
                                                    if (tAct == todayActivities[todayActivities.size - 1] || a == todayActivities[todayActivities.size - 1]) {
                                                        currentActivities += a
                                                    } else {
                                                        currentActivities += a + "&&"
                                                    }
                                                }
                                            }
                                            Modify(
                                                DayActivities(date, currentActivities),
                                                response = {},
                                                context
                                            )
                                            showMenu = !showMenu
                                        }
                                        Toast.makeText(
                                            context,
                                            "You have deleted the activity $tAct at the day $date",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                )
                            }

                            Card(
                                elevation = CardDefaults.cardElevation(5.dp),
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = LightBlue)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = today.day.toString(), modifier = Modifier
                                            .weight(1.0f)
                                            .padding(start = 20.dp)
                                    )
                                    Text(
                                        text = tAct,
                                        fontSize = 18.sp,
                                        modifier = Modifier.padding(10.dp).weight(2.0f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}