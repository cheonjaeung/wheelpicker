package com.example.snappicker.android.compose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.woong.snappicker.compose.ExperimentalSnapPickerApi
import io.woong.snappicker.compose.VerticalSnapPicker
import io.woong.snappicker.compose.rememberSnapPickerState

public class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalSnapPickerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val pickerState = rememberSnapPickerState(
                values = (0..19).map { it },
                initialIndex = 9
            )
            LaunchedEffect(pickerState.index) {
                Log.d("MainActivity", "index=${pickerState.index}, value=${pickerState.value}")
            }
            VerticalSnapPicker(
                state = pickerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White),
                repeated = true,
                decorationBox = { innerPicker ->
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x11000000))
                    )
                    innerPicker()
                },
                itemContent = { value ->
                    BasicText(
                        text = value.toString(),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            )
        }
    }
}
