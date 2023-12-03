package com.example.memominder

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
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
            Text(text = "Front Page")
        }

        ConsultaArticulo()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultaArticulo() {
    val contexto = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        var fecha by remember { mutableStateOf("") }
        var actividad by remember { mutableStateOf("") }
        var mensaje by remember { mutableStateOf("") }
        OutlinedTextField(
            value = fecha,
            onValueChange = { fecha = it },
            label = {
                Text("Fecha")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            value = actividad,
            onValueChange = { actividad = it },
            label = {
                Text("Actividad")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            singleLine = true
        )
        Button(
            onClick = {
                ConsultDate(
                    date = fecha,
                    response = {
                        if (it!=null) {
                            actividad = it.activities
                            mensaje=""
                        } else {
                            mensaje = "No existe el código de producto ingresado"
                            actividad=""
                        }
                    },
                    context = contexto
                )
            },
            modifier = Modifier.padding(10.dp)
        ) {
            Text(text = "Consultar por código")
        }
        Button(
            onClick = {
                ConsultDate(
                    date = fecha,
                    response = {
                        if (it!=null) {
                            var actividadesActual = it.activities.split("&&")
                            if (actividadesActual.contains(actividad)) {
                                mensaje = "Ya tienes puesta esta actividad este día"
                            } else {
                                var actividades = ""
                                for (i in actividadesActual) {
                                    actividades += i + "&&"
                                }
                                actividades += actividad
                                Modify(
                                    activities = DayActivities(fecha, actividades),
                                    response = {
                                        if (it)
                                            mensaje="Los datos fueron modificados"
                                        else
                                            mensaje = "No existe el código de producto ingresado"
                                    }, context = contexto
                                )
                            }
                            actividad = ""
                            mensaje=""
                        } else {
                            AltaFecha(
                                date = fecha,
                                activities = actividad,
                                context = contexto,
                                response = {
                                    if (it) {
                                        mensaje = "se cargaron los datos"
                                        fecha=""
                                        actividad=""
                                    }
                                    else
                                        mensaje="problemas en la carga"
                                }
                            )
                            mensaje = "No existe el código de producto ingresado"
                            actividad=""
                        }
                    }, context = contexto
                )
            },
            modifier = Modifier.padding(10.dp)
        ) {
            Text(text = "Agregar")
        }
        Button(
            onClick = {
                ConsultDate(
                    date = fecha,
                    response = {
                        if (it!=null) {
                            var actividadesActual = it.activities.split("&&")
                            if (actividadesActual.size == 1) {
                                Delete(
                                    date = fecha,
                                    response = {
                                        if (it) {
                                            mensaje = "Se eliminó el artículo"
                                            fecha=""
                                            actividad=""
                                        } else
                                            mensaje = "No existe el código de producto ingresado"
                                    }, contexto
                                )
                            } else {
                                var actividades = ""
                                for (i in actividadesActual) {
                                    if (i != actividad) {
                                        if (i == actividadesActual[actividadesActual.size - 1]) {
                                            actividades += i
                                        } else {
                                            actividades += i + "&&"
                                        }
                                    }
                                }
                                Modify(
                                    activities = DayActivities(fecha, actividades),
                                    response = {
                                        if (it)
                                            mensaje="Los datos fueron modificados"
                                        else
                                            mensaje = "No existe el código de producto ingresado"
                                    }, context = contexto
                                )
                            }
                            actividad = ""
                            mensaje=""
                        } else {
                            mensaje = "No hay nada que borrar en esa fecha o no existe esa fecha"
                        }
                    }, context = contexto
                )
            },
            modifier = Modifier.padding(10.dp)
        ) {
            Text(text = "Borrar")
        }
        Button(onClick = { ConsultDate(
            date = fecha,
            response = {
                if (it!=null) {
                    actividad = it.activities
                    mensaje=""
                } else {
                    mensaje = "No existe el código de producto ingresado"
                    actividad=""
                }
            },
            context = contexto
        )
        }, modifier = Modifier.padding(10.dp)) {
            Text(text = "Listar")
        }
        listDay(date = fecha, activities = actividad)
        Text(text = "$mensaje")
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

@Composable
fun listDay(date: String, activities: String){
    val todayActivities = activities.split("&&")
    val context = LocalContext.current

    if (activities != "") {
        Column {
            for (act in todayActivities) {
                var showMenu by remember { mutableStateOf(false) }
                var updatedActivities by remember {
                    mutableStateOf("")
                }
                Row(modifier = Modifier.clickable{showMenu = !showMenu}) {
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        Modifier.width(150.dp)
                    ) {
                        DropdownMenuItem(
                            text = { Text(text = "Delete activity", color = Color.Black) },
                            onClick = { if (todayActivities.size == 1) {
                                Delete(date, response = {}, context = context)
                                updatedActivities = "deleted"
                            } else {
                                for (a in todayActivities) {
                                    if (a != act) {
                                        if (act == todayActivities[todayActivities.size - 1] || a == todayActivities[todayActivities.size - 1]) {
                                            updatedActivities += a
                                        } else {
                                            updatedActivities += a + "&&"
                                        }
                                    }
                                }
                                Modify(DayActivities(date, updatedActivities), response = {}, context)
                            }
                            Toast.makeText(context, "You have deleted the activity $act at the day $date", Toast.LENGTH_LONG).show()
                            }
                        )
                    }

                    if (updatedActivities == "") {
                        Card(
                            elevation = CardDefaults.cardElevation(5.dp),
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth()
                        ) {
                            Column() {
                                Text(
                                    text = "Date: ${date}",
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
                    } else if (updatedActivities == "deleted") {

                    } else {
                        listDay(date = date, activities = updatedActivities)
                    }
                }
            }
        }
    }
}

