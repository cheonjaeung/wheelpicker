package com.example.snappicker.basicpicker

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import com.example.snappicker.databinding.FragmentBasicNumberPickerSampleBinding
import io.woong.snappicker.SnapPickerAdapter
import kotlin.math.roundToInt

public class BasicNumberPickerSample : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentBasicNumberPickerSampleBinding.inflate(inflater, container, false)
        val adapter = BasicNumberPickerAdapter()
        val values = (0..29).toList()
        adapter.setValues(values)
        binding.picker.adapter = adapter
        binding.picker.setOnValueSelectedListener { _, position ->
            Log.d("cjw", "value=${values[position]}")
        }
        return binding.root
    }
}

private class BasicNumberPickerAdapter : SnapPickerAdapter<Int, AppCompatTextView>() {
    override fun getMaxItemSize(context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            48f,
            context.resources.displayMetrics
        ).roundToInt()
    }

    override fun createItemView(context: Context): AppCompatTextView {
        val textView = AppCompatTextView(context)
        textView.gravity = Gravity.CENTER
        return textView
    }

    override fun bindItemView(itemView: AppCompatTextView, position: Int) {
        val value = getValue(position)
        itemView.text = value.toString()
    }
}
