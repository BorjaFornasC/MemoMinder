package com.example.memominder

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        convertMillisToDate(it)
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        DatePicker(
            state = datePickerState
        )
        Spacer(
            modifier = Modifier.height(
                32.dp
            )
        )
        var actividadesDia = ""
        var mensaje by remember { mutableStateOf("") }
        ConsultaFecha(
            fecha = selectedDate.toString(),
            respuesta = {
                if (it!=null) {
                    actividadesDia = it.actividades
                } else {
                    mensaje = "No hay actividades"
                }
            }, contexto
        )
        Listar(fecha = selectedDate.toString(), actividades = actividadesDia)

        Text(text = mensaje)

        Text(text = "" + datePickerState.selectedDateMillis?.let { formatedDate(it) })

        Text(
            text = selectedDate.toString(),
            color = Color.Red
        )
    }
}

@SuppressLint("SimpleDateFormat")
private fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy")
    return formatter.format(Date(millis))
}


@SuppressLint("SimpleDateFormat")
fun formatedDate(millis: Long): String {
    val date = Date(millis)
    val day = SimpleDateFormat("d").format(date)
    val month = SimpleDateFormat("MMMM").format(date)
    val year = SimpleDateFormat("yyyy").format(date)

    return "$day${when(day){
        "1", "21", "31" -> "st"
        "2", "22", "32" -> "nd"
        "3", "23" -> "rd"
        else -> "th"
    }} of $month of $year"

    /*val formated = SimpleDateFormat("d'${
        when (date.day) {
            1, 21, 31 -> "st"
            2, 22 -> "nd"
            3, 23 -> "rd"
            else -> "th"
        }
    } of' MMMM 'of' yyyy", Locale.ENGLISH)

     */

    //return formated.format(date)
}
