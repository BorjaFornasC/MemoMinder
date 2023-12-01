package com.example.memominder

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
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

        Box(modifier = Modifier.fillMaxSize().padding(bottom = it.calculateBottomPadding())) {
            DatePickerView()
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerView() {

    val contexto = LocalContext.current
    val datePickerState = rememberDatePickerState(selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            return true
        }
    })
    val selectedDate = datePickerState.selectedDateMillis?.let {
        //convertMillisToDate(it)
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
        var actividadesDia by remember {
            mutableStateOf("")
        }
        var fecha by remember {
            mutableStateOf("")
        }
        var mensaje by remember { mutableStateOf("") }

        Text(text = selectedDate.toString())
        
        Spacer(modifier = Modifier.size(20.dp))
        
        Button(onClick = {
            ConsultaFecha(
                fecha = selectedDate.toString(),
                respuesta = {
                    if (it!=null) {
                        actividadesDia = it.actividades
                        fecha = selectedDate.toString()
                        mensaje = ""
                    } else {
                        mensaje = "No hay actividades"
                    }
                }, contexto
            )

        }) {
            Text(text = "Consultar")
        }

        Column {

            if (fecha != "") {
                Listar(fecha = fecha, actividades = actividadesDia)
            }

            Text(text = mensaje)

        }
    }
}

@SuppressLint("SimpleDateFormat")
fun formatedDate(millis: Long): String {
    val date = Date(millis)
    val day = SimpleDateFormat("d").format(date)
    val month = SimpleDateFormat("MMMM").format(date)
    val year = SimpleDateFormat("yyyy").format(date)

    return "$month $day, $year"
}

