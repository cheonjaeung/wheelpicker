package com.example.snappicker.basicpicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.snappicker.databinding.FragmentBasicNumberPickerSampleBinding

public class BasicNumberPickerSample : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentBasicNumberPickerSampleBinding.inflate(inflater, container, false)
        return binding.root
    }
}
