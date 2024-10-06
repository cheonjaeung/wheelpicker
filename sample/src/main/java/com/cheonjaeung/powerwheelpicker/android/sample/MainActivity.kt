package com.cheonjaeung.powerwheelpicker.android.sample

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.cheonjaeung.powerwheelpicker.android.WheelPicker

class MainActivity : AppCompatActivity() {
    private lateinit var wheelPicker: WheelPicker
    private lateinit var selectedTextView: TextView
    private lateinit var isScrolledTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        wheelPicker = findViewById(R.id.wheelPicker)
        selectedTextView = findViewById(R.id.selected)
        isScrolledTextView = findViewById(R.id.isScrolled)
        wheelPicker.adapter = Adapter()

        wheelPicker.addOnScrollListener(
            object : WheelPicker.OnScrollListener() {
                @SuppressLint("SetTextI18n")
                override fun onScrollStateChanged(wheelPicker: WheelPicker, newState: Int) {
                    isScrolledTextView.text = "isScrolling: ${newState != WheelPicker.SCROLL_STATE_IDLE}"
                }
            }
        )

        wheelPicker.addOnItemSelectedListener { _, position ->
            @SuppressLint("SetTextI18n")
            selectedTextView.text = "Selected: $position"
        }
    }
}

private class Adapter : RecyclerView.Adapter<Holder>() {
    private val items: List<Int> = (0..99).toList()

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.view_holder_item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position].toString())
    }
}

private class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(text: String) {
        val textView = itemView.findViewById<TextView>(R.id.text)
        textView.text = text
    }
}
