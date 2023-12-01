package com.example.memominder

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.input.pointer.pointerInput
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
fun portada(navController: NavHostController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            MyNavigationBar(navHostController = navController)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) {

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Portada")
        }
        //pruebas
        ConsultaArticulo()

        /*var mapDiary by remember {
            mutableStateOf( mutableMapOf<Date, String>() )
        }

         */
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
                ConsultaFecha(
                    fecha = fecha,
                    respuesta = {
                        if (it!=null) {
                            actividad = it.actividades
                            mensaje=""
                        } else {
                            mensaje = "No existe el código de producto ingresado"
                            actividad=""
                        }
                    },
                    contexto = contexto
                )
            },
            modifier = Modifier.padding(10.dp)
        ) {
            Text(text = "Consultar por código")
        }
        Button(
            onClick = {
                ConsultaFecha(
                    fecha = fecha,
                    respuesta = {
                        if (it!=null) {
                            var actividadesActual = it.actividades.split("&&")
                            if (actividadesActual.contains(actividad)) {
                                mensaje = "Ya tienes puesta esta actividad este día"
                            } else {
                                var actividades = ""
                                for (i in actividadesActual) {
                                    actividades += i + "&&"
                                }
                                actividades += actividad
                                Modificar(
                                    actividades = ActividadesDia(fecha, actividades),
                                    respuesta = {
                                        if (it)
                                            mensaje="Los datos fueron modificados"
                                        else
                                            mensaje = "No existe el código de producto ingresado"
                                    }, contexto = contexto
                                )
                            }
                            actividad = ""
                            mensaje=""
                        } else {
                            AltaFecha(
                                fecha = fecha,
                                actividades = actividad,
                                contexto = contexto,
                                respuesta = {
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
                    }, contexto = contexto
                )
            },
            modifier = Modifier.padding(10.dp)
        ) {
            Text(text = "Agregar")
        }
        Button(
            onClick = {
                ConsultaFecha(
                    fecha = fecha,
                    respuesta = {
                        if (it!=null) {
                            var actividadesActual = it.actividades.split("&&")
                            if (actividadesActual.size == 1) {
                                Borrar(
                                    fecha = fecha,
                                    respuesta = {
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
                                Modificar(
                                    actividades = ActividadesDia(fecha, actividades),
                                    respuesta = {
                                        if (it)
                                            mensaje="Los datos fueron modificados"
                                        else
                                            mensaje = "No existe el código de producto ingresado"
                                    }, contexto = contexto
                                )
                            }
                            actividad = ""
                            mensaje=""
                        } else {
                            mensaje = "No hay nada que borrar en esa fecha o no existe esa fecha"
                        }
                    }, contexto = contexto
                )
            },
            modifier = Modifier.padding(10.dp)
        ) {
            Text(text = "Borrar")
        }
        Button(onClick = { ConsultaFecha(
            fecha = fecha,
            respuesta = {
                if (it!=null) {
                    actividad = it.actividades
                    mensaje=""
                } else {
                    mensaje = "No existe el código de producto ingresado"
                    actividad=""
                }
            },
            contexto = contexto
        )
        }, modifier = Modifier.padding(10.dp)) {
            Text(text = "Listar")
        }
        Listar(fecha = fecha, actividades = actividad)
        Text(text = "$mensaje")
    }
}

data class ActividadesDia(val fecha: String, val actividades: String)

fun ConsultaFecha(fecha: String, respuesta: (ActividadesDia?) -> Unit, contexto: Context) {
    val requestQueue = Volley.newRequestQueue(contexto)
    val url = "https://memominder.000webhostapp.com/calendario/listaractividades.php?fecha=$fecha"
    val requerimiento = JsonArrayRequest(
        Request.Method.GET,
        url,
        null,
        { response ->
            if (response.length() == 1) {
                try {
                    val objeto = JSONObject(response[0].toString())
                    val actividad = ActividadesDia(
                        objeto.getString("fecha"),
                        objeto.getString("actividades")
                    )
                    respuesta(actividad)
                } catch (e: JSONException) {
                }
            }
            else
                respuesta(null);
        }
    ) { error ->
    }
    requestQueue.add(requerimiento)
}

fun AltaFecha(fecha: String, actividades: String, contexto: Context, respuesta:
    (Boolean) -> Unit) {
    val requestQueue = Volley.newRequestQueue(contexto)
    val url = "https://memominder.000webhostapp.com/calendario/insertar.php"
    val parametros=JSONObject()
    parametros.put("fecha",fecha)
    parametros.put("actividades",actividades)
    val requerimiento = JsonObjectRequest(Request.Method.POST,
        url,
        parametros,
        { response ->
            if (response.get("respuesta").toString().equals("ok"))
                respuesta(true)
            else
                respuesta(false)
        },
        { error ->
            respuesta(false)
        }
    )
    requestQueue.add(requerimiento)
}

fun Modificar(actividades: ActividadesDia, respuesta: (Boolean) -> Unit, contexto: Context)
{
    val requestQueue = Volley.newRequestQueue(contexto)
    val url = "https://memominder.000webhostapp.com/calendario/modificar.php"
    val parametros = JSONObject()
    parametros.put("fecha", actividades.fecha)
    parametros.put("actividades", actividades.actividades)
    val requerimiento = JsonObjectRequest(
        Request.Method.POST,
        url,
        parametros,
        { response ->
            try {
                val resu = response["resultado"].toString()
                if (resu == "1")
                    respuesta(true)
                else
                    respuesta(false)
            } catch (e: JSONException) {
                respuesta(false)
            }
        }
    ) { error -> respuesta(false) }
    requestQueue.add(requerimiento)
}

fun Borrar(fecha: String, respuesta: (Boolean) -> Unit, contexto: Context) {
    val requestQueue = Volley.newRequestQueue(contexto)
    val url = "https://memominder.000webhostapp.com/calendario/borrar.php"
    val parametros = JSONObject()
    parametros.put("fecha", fecha)
    val requerimiento = JsonObjectRequest(
        Request.Method.POST,
        url,
        parametros,
        { response ->
            try {
                val resu = response["resultado"].toString()
                if (resu == "1")
                    respuesta(true)
                else
                    respuesta(false)
            } catch (e: JSONException) {
                respuesta(false)
            }
        }
    ) { error -> respuesta(false) }
    requestQueue.add(requerimiento)
}

@Composable
fun Listar(fecha: String,actividades: String){
    val actividadesHoy = actividades.split("&&")
    val contexto = LocalContext.current

    if (actividades != "") {
        Column {
            for (act in actividadesHoy) {
                var showMenu by remember { mutableStateOf(false) }
                var actividadesActualizadas by remember {
                    mutableStateOf("")
                }
                Row(modifier = Modifier.clickable{showMenu = !showMenu})
                {
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        Modifier.width(150.dp)
                    ) {
                        DropdownMenuItem(
                            text = { Text(text = "Borrar actividad", color = Color.Black) },
                            onClick = { if (actividadesHoy.size == 1) {
                                Borrar(fecha, respuesta = {}, contexto = contexto)
                                actividadesActualizadas = "borrado"
                            } else {
                                for (a in actividadesHoy) {
                                    if (a != act) {
                                        if (act == actividadesHoy[actividadesHoy.size - 1] || a == actividadesHoy[actividadesHoy.size - 1]) {
                                            actividadesActualizadas += a
                                        } else {
                                            actividadesActualizadas += a + "&&"
                                        }
                                    }
                                }
                                Modificar(ActividadesDia(fecha, actividadesActualizadas), respuesta = {}, contexto)
                            }
                            Toast.makeText(contexto, "Has borrado la actividad $act del día $fecha", Toast.LENGTH_LONG).show()
                            }
                        )
                    }

                    if (actividadesActualizadas == "" || actividadesActualizadas == "borrado") {
                        Card(
                            elevation = CardDefaults.cardElevation(5.dp),
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth()
                        ) {
                            Column() {
                                Text(
                                    text = "Fecha: ${fecha}",
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(
                                        start = 10.dp, end = 10.dp,
                                        bottom = 5.dp
                                    )
                                )
                                Text(
                                    text = "Actividad: ${act}",
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    } else {
                        Listar(fecha = fecha, actividades = actividadesActualizadas)
                    }
                }
            }
        }
    }
}

