package com.example.wheelpicker.basicpicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.example.wheelpicker.databinding.FragmentBasicNumberPickerComposeSampleBinding
import io.woong.wheelpicker.compose.ValuePicker
import io.woong.wheelpicker.compose.rememberValuePickerState

public class BasicNumberPickerComposeSample : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentBasicNumberPickerComposeSampleBinding.inflate(inflater, container, false)
        binding.composeView.setContent {
            SampleContent()
        }
        return binding.root
    }
}

@Composable
private fun SampleContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xfff9f9f9))
    ) {
        val values = remember { (0..23).toList() }
        val state = rememberValuePickerState(initialValue = values[3])

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            BasicText(text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("Selected Value: ")
                }
                withStyle(SpanStyle(fontSize = 18.sp, color = Color.Gray)) {
                    append("${state.currentValue}")
                }
            })
            BasicText(text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                ) {
                    append("Scrolling: ")
                }
                withStyle(SpanStyle(fontSize = 18.sp, color = Color.Gray)) {
                    append("${state.isScrollInProgress}")
                }
            })
        }
        ValuePicker(
            values = values,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White),
            state = state,
            isCyclic = true,
            decorationBox = { innerPicker ->
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 8.dp)
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE9E9E9))
                )
                innerPicker()
            }
        ) { value ->
            Box(modifier = Modifier.fillMaxSize()) {
                BasicText(text = value.toString(), modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}
