package com.example.memominder

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun frontPage(navController: NavHostController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            MyNavigationBar(navHostController = navController)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) {

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            welcome()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun welcome() {

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.logoblack),
            contentDescription = "Logotipo Splash Screen",
            modifier = Modifier
                .size(500.dp)
        )
    }
}

data class DayActivities(val date: String, val activities: String)

fun ConsultDate(date: String, response: (DayActivities?) -> Unit, context: Context) {
    val requestQueue = Volley.newRequestQueue(context)
    val url = "https://memominder.000webhostapp.com/calendario/listaractividades.php?fecha=$date"
    val request = JsonArrayRequest(
        Request.Method.GET,
        url,
        null,
        { response ->
            if (response.length() == 1) {
                try {
                    val jsonObject = JSONObject(response[0].toString())
                    val activity = DayActivities(
                        jsonObject.getString("fecha"),
                        jsonObject.getString("actividades")
                    )
                    response(activity)
                } catch (e: JSONException) {
                }
            }
            else
                response(null);
        }
    ) { error ->
    }
    requestQueue.add(request)
}

fun AltaFecha(date: String, activities: String, context: Context, response:
    (Boolean) -> Unit) {
    val requestQueue = Volley.newRequestQueue(context)
    val url = "https://memominder.000webhostapp.com/calendario/insertar.php"
    val parameters=JSONObject()
    parameters.put("fecha",date)
    parameters.put("actividades",activities)
    val request = JsonObjectRequest(Request.Method.POST,
        url,
        parameters,
        { response ->
            if (response.get("respuesta").toString().equals("ok"))
                response(true)
            else
                response(false)
        },
        { error ->
            response(false)
        }
    )
    requestQueue.add(request)
}

fun Modify(activities: DayActivities, response: (Boolean) -> Unit, context: Context)
{
    val requestQueue = Volley.newRequestQueue(context)
    val url = "https://memominder.000webhostapp.com/calendario/modificar.php"
    val parameters = JSONObject()
    parameters.put("fecha", activities.date)
    parameters.put("actividades", activities.activities)
    val request = JsonObjectRequest(
        Request.Method.POST,
        url,
        parameters,
        { response ->
            try {
                val resu = response["resultado"].toString()
                if (resu == "1")
                    response(true)
                else
                    response(false)
            } catch (e: JSONException) {
                response(false)
            }
        }
    ) { error -> response(false) }
    requestQueue.add(request)
}

fun Delete(date: String, response: (Boolean) -> Unit, context: Context) {
    val requestQueue = Volley.newRequestQueue(context)
    val url = "https://memominder.000webhostapp.com/calendario/borrar.php"
    val parameters = JSONObject()
    parameters.put("fecha", date)
    val request = JsonObjectRequest(
        Request.Method.POST,
        url,
        parameters,
        { response ->
            try {
                val resu = response["resultado"].toString()
                if (resu == "1")
                    response(true)
                else
                    response(false)
            } catch (e: JSONException) {
                response(false)
            }
        }
    ) { error -> response(false) }
    requestQueue.add(request)
}

@SuppressLint("MutableCollectionMutableState")
@Composable
fun listDay(date: String, activities: String){
    val activitiesSplited by remember {
        mutableStateOf(activities.split("&&"))
    }
    var todayActivities = mutableListOf<String>()
    for (a in activitiesSplited) {
        todayActivities.add(a)
    }
    val context = LocalContext.current
    var currentDate by remember {
        mutableStateOf(date)
    }
    var updatedActivities by remember {
        mutableStateOf(todayActivities.size )
    }

    if (activities != "" && currentDate == date) {
        Column {
            for (act in todayActivities) {
                var showMenu by remember { mutableStateOf(false) }
                var currentActivities by remember {
                    mutableStateOf("")
                }
                Row(modifier = Modifier.clickable{showMenu = !showMenu}) {

                    if (currentActivities == "") {
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            Modifier.width(150.dp)
                        ) {
                            DropdownMenuItem(
                                text = { Text(text = "Delete activity", color = Color.Black) },
                                onClick = { if (updatedActivities == 1) {
                                    Delete(date, response = {}, context = context)
                                    currentDate = ""
                                    todayActivities.remove(act)
                                } else {
                                    currentActivities = ""
                                    for (a in todayActivities) {
                                        if (a != act) {
                                            if (act == todayActivities[todayActivities.size - 1] || a == todayActivities[todayActivities.size - 1]) {
                                                currentActivities += a
                                            } else {
                                                currentActivities += a + "&&"
                                            }
                                        }
                                    }
                                    Modify(DayActivities(date, currentActivities), response = {}, context)
                                    todayActivities.remove(act)
                                    updatedActivities--
                                }
                                    Toast.makeText(context, "You have deleted the activity $act at the day $date", Toast.LENGTH_LONG).show()
                                }
                            )
                        }
                        Card(
                            elevation = CardDefaults.cardElevation(5.dp),
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth()
                        ) {
                            Column() {
                                Text(
                                    text = "Date: ${printDate(date)}",
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(
                                        start = 10.dp, end = 10.dp,
                                        bottom = 5.dp
                                    )
                                )
                                Text(
                                    text = "Activity: ${act}",
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
