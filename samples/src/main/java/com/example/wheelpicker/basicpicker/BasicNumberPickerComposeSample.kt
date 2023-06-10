package com.example.wheelpicker.basicpicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.fragment.app.Fragment
import com.example.wheelpicker.databinding.FragmentBasicNumberPickerComposeSampleBinding

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

    }
}
