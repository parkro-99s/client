package com.parkro.client.domain.admin_parkinglist.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.parkro.client.R
import com.parkro.client.databinding.ItemParkinglistBinding
import com.parkro.client.domain.admin_parkinglist.api.GetAdminParkingRes
import com.parkro.client.util.DateFormatUtil

/**
 * 관리자 주차 목록 조회 시 사용되는 RecyclerView Adapter
 *
 * @author 김지수
 * @since 2024.08.05
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.08.05   김지수      최초 생성
 * 2024.08.05   김지수      관리자 주차 목록 조회
 * </pre>
 */
class AdminParkingListRecyclerAdapter(
    private var adminParkingList: MutableList<GetAdminParkingRes>,
    private val onItemClicked: (Int) -> Unit
) : RecyclerView.Adapter<AdminParkingListRecyclerAdapter.AdminParkingListViewHolder>() {

    inner class AdminParkingListViewHolder(private val binding: ItemParkinglistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(parking: GetAdminParkingRes) {
            Log.d("AdminParkingListRecyclerAdapter", "try to binding $parking")
            binding.textParkinglistStoreName.text = parking.storeName
            binding.textParkinglistParkingLotName.text = parking.parkingLotName
            binding.textParkinglistCarNumber.text = parking.carNumber
            binding.btnParkinglistEntranceDate.text = "입차 ${DateFormatUtil.formatDate(parking.entranceDate)}"

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
                    binding.btnParkinglistChevron.visibility = View.VISIBLE
                }
                else -> {
                    binding.textParkinglistLabel.visibility = View.GONE
                    binding.btnParkinglistChevron.visibility = View.VISIBLE
                }
            }

            binding.root.setOnClickListener {
                onItemClicked(parking.parkingId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminParkingListViewHolder {
        val binding =
            ItemParkinglistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdminParkingListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdminParkingListViewHolder, position: Int) {
        Log.d("AdminParkingListRecyclerAdapter", "bind view holder: $position")
        holder.bind(adminParkingList[position])
    }

    override fun getItemCount(): Int {
        return adminParkingList.size
    }

    fun setItems(newItems: List<GetAdminParkingRes>) {
        adminParkingList.clear()
        adminParkingList.addAll(newItems)
        notifyDataSetChanged()
    }
}
