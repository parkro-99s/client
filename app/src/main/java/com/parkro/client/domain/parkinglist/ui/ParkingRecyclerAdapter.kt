package com.parkro.client.domain.parkinglist.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.parkro.client.R
import com.parkro.client.databinding.ItemParkinglistBinding
import com.parkro.client.domain.parkinglist.api.GetParkingRes
import com.parkro.client.util.DateFormatUtil

class ParkingRecyclerAdapter(
    private val parkingList: MutableList<GetParkingRes>,
    private val onItemClicked: (Int) -> Unit
) : RecyclerView.Adapter<ParkingRecyclerAdapter.ParkingRecyclerViewHolder>() {

    inner class ParkingRecyclerViewHolder(private val binding: ItemParkinglistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(parking: GetParkingRes) {
            binding.textParkinglistStoreName.text = parking.storeName
            binding.textParkinglistParkingLotName.text = parking.parkingLotName
            binding.textParkinglistCarNumber.text = parking.carNumber
            binding.btnParkinglistEntranceDate.text = DateFormatUtil.formatDate(parking.entranceDate)

            when (parking.status) {
                "PAY" -> {
                    binding.textParkinglistLabel.text = binding.root.context.getString(R.string.label_parkinglist_pay_done)
                    binding.textParkinglistLabel.setBackgroundResource(R.drawable.label_orange)
                    binding.textParkinglistLabel.visibility = View.VISIBLE
                    binding.btnParkinglistChevron.visibility = View.VISIBLE
                }
                "EXIT" -> {
                    binding.textParkinglistLabel.text = binding.root.context.getString(R.string.label_parkinglist_exit_done)
                    binding.textParkinglistLabel.setBackgroundResource(R.drawable.label_navy)
                    binding.textParkinglistLabel.visibility = View.VISIBLE
                    binding.btnParkinglistChevron.visibility = View.VISIBLE
                }
                "ENTRANCE" -> {
                    binding.textParkinglistLabel.visibility = View.GONE
                    binding.btnParkinglistChevron.visibility = View.GONE
                }
                else -> {
                    binding.textParkinglistLabel.visibility = View.GONE
                    binding.btnParkinglistChevron.visibility = View.GONE
                }
            }

            binding.root.setOnClickListener {
                if (parking.status != "ENTRANCE") {
                    onItemClicked(parking.parkingId)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkingRecyclerViewHolder {
        val binding =
            ItemParkinglistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ParkingRecyclerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ParkingRecyclerViewHolder, position: Int) {
        holder.bind(parkingList[position])
    }

    override fun getItemCount(): Int {
        return parkingList.size
    }

    fun addItems(newItems: List<GetParkingRes>) {
        parkingList.clear()
        parkingList.addAll(newItems)
        notifyDataSetChanged()
    }
}