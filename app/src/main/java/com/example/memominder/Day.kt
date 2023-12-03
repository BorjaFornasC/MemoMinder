package com.example.memominder

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.memominder.ui.theme.FontTittle

@Composable
fun day(date : String, navController: NavHostController){

    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
        var activity by remember {
            mutableStateOf("")
        }
        var message by remember {
            mutableStateOf("")
        }
        val context = LocalContext.current
        
        Text(text = date, fontFamily = FontTittle, fontSize = 30.sp, modifier = Modifier.padding(vertical = 30.dp))

        Text(text = "Add new activity (Don't put &&)", modifier = Modifier.padding(bottom = 10.dp))

        OutlinedTextField(
            value = activity,
            onValueChange = { activity = it },
            label = {
                Text("Activity")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        
        Spacer(modifier = Modifier.size(20.dp))

        Button(onClick = {
            ConsultDate(
                date = date,
                response = {
                    if (it!=null) {
                        val currentActivities = it.activities.split("&&")
                        if (currentActivities.contains(activity)) {
                            message = "You already have this activity set for this day"
                        } else {
                            var activities = ""
                            for (i in currentActivities) {
                                activities += i + "&&"
                            }
                            activities += activity
                            Modify(
                                activities = DayActivities(date, activities),
                                response = {
                                    if (it)
                                        message="The activity is added"
                                    else
                                        message = "The date entered does not exist"
                                }, context = context
                            )
                        }
                    } else {
                        AltaFecha(
                            date = date,
                            activities = activity,
                            context = context,
                            response = {
                                if (it) {
                                    message = "The activity is added"
                                }
                                else
                                    message="Problems at the adding"
                            }
                        )
                    }
                }, context = context
            )
        }) {
            Text(text = "Add")
        }

        if (activity != "") {
            var dayActivities by remember {
                mutableStateOf("")
            }
            ConsultDate(date, response = {
                if (it != null) {
                    dayActivities = it.activities
                }
            }, context)
            if (dayActivities != "") {
                listDay(date = date, activities = dayActivities)
            }
        }

        Button(onClick = { navController.navigate("Calendar") }) {
            Text(text = "Return to the calendar")
        }

        Text(text = message)

    }
}