package com.parkro.client.domain.parkinglist.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.parkro.client.R
import com.parkro.client.databinding.ItemParkinglistBinding
import com.parkro.client.domain.parkinglist.api.GetParkingRes
import com.parkro.client.util.DateFormatUtil

/**
 * 주차장 목록 리사이클러 어댑터
 *
 * @author 김민정
 * @since 2024.08.03
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.08.03   김민정       최초 생성
 * </pre>
 */
class ParkingRecyclerAdapter(
    private val parkingList: MutableList<GetParkingRes>,
    private val onItemClicked: (Int) -> Unit
) : RecyclerView.Adapter<ParkingRecyclerAdapter.ParkingRecyclerViewHolder>() {

    // 주차 내역 뷰 관리
    inner class ParkingRecyclerViewHolder(private val binding: ItemParkinglistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // 주차 데이터 UI에 바인딩
        fun bind(parking: GetParkingRes) {
            binding.textParkinglistStoreName.text = parking.storeName
            binding.textParkinglistParkingLotName.text = parking.parkingLotName
            binding.textParkinglistCarNumber.text = parking.carNumber
            binding.btnParkinglistEntranceDate.text = "입차 ${DateFormatUtil.formatDate(parking.entranceDate)}"

            // 주차 상태에 따른 라벨 및 UI 요소 변경
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

            // 항목 클릭 시, 입차 상태 아닌 경우에만 콜백 함수 호출
            binding.root.setOnClickListener {
                if (parking.status != "ENTRANCE") {
                    onItemClicked(parking.parkingId)
                }
            }
        }
    }

    // ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkingRecyclerViewHolder {
        val binding =
            ItemParkinglistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ParkingRecyclerViewHolder(binding)
    }

    // ViewHolder에 데이터 바인딩
    override fun onBindViewHolder(holder: ParkingRecyclerViewHolder, position: Int) {
        holder.bind(parkingList[position])
    }

    // 아이템 총 개수 반환
    override fun getItemCount(): Int {
        return parkingList.size
    }

    // 기존 데이터 지우기 + 새 데이터 추가
    fun addItems(newItems: List<GetParkingRes>) {
        parkingList.clear()
        parkingList.addAll(newItems)
        notifyDataSetChanged()
    }
}