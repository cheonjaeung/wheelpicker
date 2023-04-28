package com.example.snappicker.basicpicker

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import com.example.snappicker.R
import com.example.snappicker.databinding.FragmentBasicNumberPickerSampleBinding
import com.example.snappicker.databinding.ViewBasicNumberPickerItemBinding
import io.woong.snappicker.ValuePickerAdapter

public class BasicNumberPickerSample : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentBasicNumberPickerSampleBinding.inflate(inflater, container, false)
        val values = (0..23).toList()
        binding.selectedValue.text = values[0].toString()
        val adapter = BasicNumberPickerAdapter()
        adapter.values = values
        binding.picker.adapter = adapter
        binding.picker.isCyclic = true
        binding.picker.setOnValueSelectedListener { _, position ->
            binding.selectedValue.text = values[position].toString()
        }
        return binding.root
    }
}

private class BasicNumberPickerAdapter : ValuePickerAdapter<Int, View>() {
    override fun createItemView(context: Context): View {
        val binding = ViewBasicNumberPickerItemBinding.inflate(LayoutInflater.from(context))
        return binding.root
    }

    override fun bindItemView(itemView: View, position: Int) {
        val value = getValue(position)
        itemView.findViewById<AppCompatTextView>(R.id.valueText).text = value.toString()
    }
}
