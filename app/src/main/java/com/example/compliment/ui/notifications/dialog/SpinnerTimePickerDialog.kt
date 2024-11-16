package com.example.compliment.ui.notifications.dialog

import android.widget.DatePicker
import androidx.appcompat.view.ContextThemeWrapper
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.compliment.R
import java.util.Calendar

@Composable
fun SpinnerTimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, initialHour)
        set(Calendar.MINUTE, initialMinute)
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Выберите время") },
        text = {
            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { ctx ->
                    DatePicker(ContextThemeWrapper(ctx, R.style.SpinnerDatePickerStyle)).apply {
                      //  setIs24HourView(true)
//                        currentHour = initialHour
//                        currentMinute = initialMinute
//                        setOnTimeChangedListener { _, hourOfDay, minute ->
//                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
//                            calendar.set(Calendar.MINUTE, minute)
//                        }
                    }
                }
            )
        },
        confirmButton = {
            TextButton(onClick = {
                onTimeSelected(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
                onDismissRequest()
            }) {
                Text("ОК")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Отмена")
            }
        }
    )
}