package com.cheonjaeung.powerwheelpicker.android.sample

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.cheonjaeung.powerwheelpicker.android.WheelPicker
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    private lateinit var wheelPicker1: WheelPicker
    private lateinit var wheelPicker2: WheelPicker
    private lateinit var wheelPicker3: WheelPicker
    private lateinit var wheelPicker4: WheelPicker

    private lateinit var adapter1: SampleAdapter
    private lateinit var adapter2: SampleAdapter
    private lateinit var adapter3: SampleAdapter
    private lateinit var adapter4: SampleAdapter

    private val isScrolling1 = MutableStateFlow(false)
    private val isScrolling2 = MutableStateFlow(false)
    private val isScrolling3 = MutableStateFlow(false)
    private val isScrolling4 = MutableStateFlow(false)
    private val isScrolling = combine(isScrolling1, isScrolling2, isScrolling3, isScrolling4) { s1, s2, s3, s4 ->
        s1 || s2 || s3 || s4
    }.stateIn(
        scope = lifecycleScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = false
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        wheelPicker1 = findViewById(R.id.wheelPicker1)
        wheelPicker2 = findViewById(R.id.wheelPicker2)
        wheelPicker3 = findViewById(R.id.wheelPicker3)
        wheelPicker4 = findViewById(R.id.wheelPicker4)

        adapter1 = SampleAdapter()
        adapter2 = SampleAdapter()
        adapter3 = SampleAdapter()
        adapter4 = SampleAdapter()
        wheelPicker1.adapter = adapter1
        wheelPicker2.adapter = adapter2
        wheelPicker3.adapter = adapter3
        wheelPicker4.adapter = adapter4

        wheelPicker1.addItemEffector(SampleItemEffector(this, wheelPicker1))
        wheelPicker2.addItemEffector(SampleItemEffector(this, wheelPicker2))
        wheelPicker3.addItemEffector(SampleItemEffector(this, wheelPicker3))
        wheelPicker4.addItemEffector(SampleItemEffector(this, wheelPicker4))

        wheelPicker1.addOnScrollListener(SampleScrollStateListener(isScrolling1))
        wheelPicker2.addOnScrollListener(SampleScrollStateListener(isScrolling2))
        wheelPicker3.addOnScrollListener(SampleScrollStateListener(isScrolling3))
        wheelPicker4.addOnScrollListener(SampleScrollStateListener(isScrolling4))

        isScrolling.onEach { isScrolling ->
            if (!isScrolling) {
                val p1 = wheelPicker1.currentPosition
                val p2 = wheelPicker2.currentPosition
                val p3 = wheelPicker3.currentPosition
                val p4 = wheelPicker4.currentPosition
                if (
                    p1 == WheelPicker.NO_POSITION ||
                    p2 == WheelPicker.NO_POSITION ||
                    p3 == WheelPicker.NO_POSITION ||
                    p4 == WheelPicker.NO_POSITION
                ) {
                    return@onEach
                }
                val n1 = adapter1.items[p1]
                val n2 = adapter1.items[p2]
                val n3 = adapter1.items[p3]
                val n4 = adapter1.items[p4]
                Snackbar.make(findViewById(R.id.main), "${n1}${n2}${n3}${n4}", 2000).show()
            }
        }.launchIn(lifecycleScope)
    }
}

private class SampleAdapter : RecyclerView.Adapter<SampleAdapter.Holder>() {
    val items: List<Int> = (0..9).toList()

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.view_holder_item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position])
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(number: Int) {
            val textView = itemView.findViewById<TextView>(R.id.text)
            textView.text = number.toString()
        }
    }
}

private class SampleItemEffector(
    context: Context,
    private val wheelPicker: WheelPicker
) : WheelPicker.ItemEffector() {
    private val greenColor = ContextCompat.getColor(context, R.color.blue)
    private val blackColor = ContextCompat.getColor(context, R.color.black)
    private var targetColor = blackColor
    private var currentColor = blackColor

    private var colorAnimator: ValueAnimator? = null

    private val wheelPickerHeight: Float
        get() = wheelPicker.measuredHeight.toFloat()

    override fun applyEffectOnScrollStateChanged(
        view: View,
        newState: Int,
        positionOffset: Int,
        centerOffset: Int
    ) {
        val textView = view.findViewById<TextView>(R.id.text)
        targetColor = if (newState != WheelPicker.SCROLL_STATE_IDLE) {
            greenColor
        } else {
            blackColor
        }
        colorAnimator = ValueAnimator.ofArgb(textView.currentTextColor, targetColor).apply {
            duration = 250
            addUpdateListener {
                currentColor = it.animatedValue as Int
                textView?.setTextColor(currentColor)
            }
        }
        colorAnimator?.start()
    }

    override fun applyEffectOnScrolled(
        view: View,
        delta: Int,
        positionOffset: Int,
        centerOffset: Int
    ) {
        view.alpha = 1f - abs(centerOffset) / (wheelPickerHeight / 2f)
        val textView = view.findViewById<TextView>(R.id.text)
        textView?.setTextColor(currentColor)
    }
}

private class SampleScrollStateListener(
    private val stateFlow: MutableStateFlow<Boolean>
) : WheelPicker.OnScrollListener() {
    override fun onScrollStateChanged(wheelPicker: WheelPicker, newState: Int) {
        val isScrolling = newState != WheelPicker.SCROLL_STATE_IDLE
        stateFlow.update { isScrolling }
    }
}
