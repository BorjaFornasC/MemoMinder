package com.example.memominder

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
fun dia(fecha : String){
    var actividadesDia by remember {
        mutableStateOf("")
    }
    var mensaje by remember {
        mutableStateOf("")
    }
    val contexto = LocalContext.current
    ConsultaFecha(
        fecha = fecha,
        respuesta = {
            if (it!=null) {
                actividadesDia = it.actividades
            } else {
                mensaje = "No hay actividades"
            }
        }, contexto
    )
    Column {

        Text(text = fecha)

        if (fecha != "") {
            Listar(fecha = fecha, actividades = actividadesDia)
        }

        Text(text = mensaje)

    }
}