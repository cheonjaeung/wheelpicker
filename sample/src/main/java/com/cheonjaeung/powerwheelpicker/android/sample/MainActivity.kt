package com.cheonjaeung.powerwheelpicker.android.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.cheonjaeung.powerwheelpicker.android.WheelPicker

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val picker = findViewById<WheelPicker>(R.id.wheelPicker)
        picker.adapter = Adapter()
    }
}

private class Adapter : RecyclerView.Adapter<Holder>() {
    private val items: List<Int> = (1..100).toList()

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
