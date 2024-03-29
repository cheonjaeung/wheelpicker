package com.example.wheelpicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wheelpicker.databinding.FragmentSampleListBinding
import com.example.wheelpicker.databinding.ViewHolderSampleListItemBinding

public class SampleListFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSampleListBinding.inflate(inflater, container, false)
        val list = binding.sampleList
        val adapter = SampleListAdapter(createSampleItems())
        adapter.setOnItemClickListener { item ->
            val direction = item.navDirections
            if (direction != null) {
                findNavController().navigate(direction)
            } else {
                Toast.makeText(requireContext(), "Not implemented yet", Toast.LENGTH_LONG).show()
            }
        }
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        return binding.root
    }

    private fun createSampleItems(): List<SampleItem> {
        val samples = mutableListOf<SampleItem>()
        samples.addAll(listOf(
            SampleItem(
                "Basic Number Picker",
                SampleListFragmentDirections.actionSampleListFragmentToBasicNumberPickerSample()
            ),
            SampleItem(
                "Basic Number Picker\n(Jetpack Compose)",
                SampleListFragmentDirections.actionSampleListFragmentToBasicNumberPickerComposeSample()
            ),
        ))
        return samples
    }
}

private class SampleListAdapter(
    private val samples: List<SampleItem>
) : RecyclerView.Adapter<SampleItemViewHolder>() {
    private var itemClickListener: OnItemClickListener? = null

    override fun getItemCount(): Int = samples.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SampleItemViewHolder {
        return SampleItemViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: SampleItemViewHolder, position: Int) {
        holder.bind(samples[position], itemClickListener)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    fun interface OnItemClickListener {
        fun onItemClick(item: SampleItem)
    }
}

private class SampleItemViewHolder private constructor(
    private val binding: ViewHolderSampleListItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(sample: SampleItem, clickListener: SampleListAdapter.OnItemClickListener?) {
        binding.title.text = sample.title
        binding.root.setOnClickListener { clickListener?.onItemClick(sample) }
    }

    companion object {
        fun create(parent: ViewGroup) : SampleItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val view = ViewHolderSampleListItemBinding.inflate(inflater, parent, false)
            return SampleItemViewHolder(view)
        }
    }
}

private data class SampleItem(
    val title: String,
    val navDirections: NavDirections?
)
