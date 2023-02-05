package com.example.snappicker.android.compose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.woong.snappicker.compose.ExperimentalSnapPickerApi
import io.woong.snappicker.compose.SnapPicker
import io.woong.snappicker.compose.rememberSnapPickerState

public class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalSnapPickerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xffefefef))
                        .padding(all = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    SnapPicker(
                        state = rememberSnapPickerState(
                            values = (0..999).map { it },
                            initialIndex = 13
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White),
                        itemContent = { value ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                BasicText(text = value.toString())
                            }
                        }
                    )

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                            .height(64.dp)
                            .background(Color(0x11000000))
                    )
                }
            }
        }
    }
}
