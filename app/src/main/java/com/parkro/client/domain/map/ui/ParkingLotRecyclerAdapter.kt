package com.parkro.client.domain.map.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.parkro.client.databinding.ItemMapParkingLotBinding
import com.parkro.client.domain.map.api.GetParkingLotRes

class ParkingLotRecyclerAdapter(private val parkingLots: List<GetParkingLotRes>) :
    RecyclerView.Adapter<ParkingLotRecyclerAdapter.ParkingLotViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkingLotViewHolder {
        val binding = ItemMapParkingLotBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ParkingLotViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: ParkingLotViewHolder, position: Int) {
        holder.bind(parkingLots[position], position)
    }

    override fun getItemCount(): Int {
        return parkingLots.size
    }

    class ParkingLotViewHolder(private val binding: ItemMapParkingLotBinding,
                               private val context: Context) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("ResourceAsColor")
        fun bind(parkingLot: GetParkingLotRes, position: Int) {
            binding.textMapItemParkingLotName.text = parkingLot.name
            binding.textMapItemAddress.text = parkingLot.address
            binding.textMapItemSpaces.text = "${parkingLot.usedSpaces}/${parkingLot.totalSpaces}"

            if (position == 0) {
                binding.dividerMap.visibility = View.GONE
            }
        }
    }
}