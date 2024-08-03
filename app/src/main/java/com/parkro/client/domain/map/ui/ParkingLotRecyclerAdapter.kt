package com.parkro.client.domain.map.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.parkro.client.R
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

//            // 첫 번째 아이템 세팅
//            if (parkingLot.isInternal == "Y") {
//                val whiteColor = ContextCompat.getColor(context, R.color.parkro_white)
//                binding.textMapItemParkingLotName.setTextColor(whiteColor)
//                binding.textMapItemAddress.setTextColor(whiteColor)
//                binding.textMapItemSpaces.setTextColor(whiteColor)
//                binding.textMapItemOutlabel.visibility = View.GONE
//                binding.dividerMap.visibility = View.GONE
//                binding.layoutMapItem.setPadding(0, 0, 0, 100)
//
//            } else {
//                binding.textMapItemOutlabel.visibility = View.VISIBLE
//            }

//            // 세 번째 아이템부터 구분선
//            if (position >= 1) {
//                binding.dividerMap.visibility = View.VISIBLE
//            } else {
//                binding.dividerMap.visibility = View.GONE
//            }

            if (position == 0) {
                binding.dividerMap.visibility = View.GONE
            }
        }
    }
}