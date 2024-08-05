package com.parkro.client.domain.admin_parkinglist.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.parkro.client.databinding.ItemStringPickerBinding

class StoreRecyclerAdapter(
    private var items: List<String>,
    private val onItemClicked: (String) -> Unit
) : RecyclerView.Adapter<StoreRecyclerAdapter.StoreViewHolder>() {

    inner class StoreViewHolder(private val binding: ItemStringPickerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.textItemName.text = item
            binding.root.setOnClickListener {
                onItemClicked(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val binding = ItemStringPickerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
