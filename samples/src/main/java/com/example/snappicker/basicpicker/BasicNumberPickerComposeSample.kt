package com.example.snappicker.basicpicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.example.snappicker.databinding.FragmentBasicNumberPickerComposeSampleBinding
import io.woong.snappicker.ExperimentalSnapPickerApi
import io.woong.snappicker.compose.VerticalSnapPicker
import io.woong.snappicker.compose.rememberSnapPickerState

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

@OptIn(ExperimentalSnapPickerApi::class)
@Composable
private fun SampleContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xfff9f9f9))
    ) {
        val values = remember { (0..99).toList() }
        val pickerState = rememberSnapPickerState()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicText(text = buildAnnotatedString {
                withStyle(SpanStyle(
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )) {
                    append("Selected Value: ")
                }
                withStyle(SpanStyle(fontSize = 18.sp)) {
                    append("${values[pickerState.currentIndex]}")
                }
            })
        }

        VerticalSnapPicker(
            values = values,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White),
            state = pickerState,
            itemContent = { value ->
                PickerItem(text = "$value")
            }
        )
    }
}

@Composable
private fun PickerItem(text: String) {
    Box(modifier = Modifier.fillMaxSize()) {
        BasicText(text = text, modifier = Modifier.align(Alignment.Center))
    }
}
