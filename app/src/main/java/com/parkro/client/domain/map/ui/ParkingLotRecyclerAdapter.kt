package com.parkro.client.domain.map.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.parkro.client.databinding.ItemMapParkingLotBinding
import com.parkro.client.domain.map.api.GetParkingLotRes
import com.parkro.client.util.ClipboardUtil

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
class ParkingLotRecyclerAdapter(private val parkingLots: List<GetParkingLotRes>) :
    RecyclerView.Adapter<ParkingLotRecyclerAdapter.ParkingLotViewHolder>() {

    // 뷰 홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkingLotViewHolder {
        val binding =
            ItemMapParkingLotBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ParkingLotViewHolder(binding, parent.context)
    }

    // 뷰 홀더에 데이터 바인딩
    override fun onBindViewHolder(holder: ParkingLotViewHolder, position: Int) {
        holder.bind(parkingLots[position], position)
    }

    // 아이템 총 개수 반환
    override fun getItemCount(): Int {
        return parkingLots.size
    }

    // 주차장 항목 뷰 관리
    class ParkingLotViewHolder(private val binding: ItemMapParkingLotBinding,
                               private val context: Context) : RecyclerView.ViewHolder(binding.root) {

        // 주차장 데이터를 UI에 바인딩
        @SuppressLint("ResourceAsColor")
        fun bind(parkingLot: GetParkingLotRes, position: Int) {
            binding.textMapItemParkingLotName.text = parkingLot.name
            binding.textMapItemAddress.text = parkingLot.address
            binding.textMapItemSpaces.text = "${parkingLot.usedSpaces}/${parkingLot.totalSpaces}"

            // 주소 텍스트에서 공백을 Non-breaking space로 변경하여 텍스트가 잘리지 않도록 설정
            binding.textMapItemAddress.text =
                binding.textMapItemAddress.text.toString().replace(" ", "\u00A0")

            if (position == 0) {
                binding.dividerMap.visibility = View.GONE
            }

            // 클립보드 복사
            binding.layoutMapCopy.setOnClickListener {
                ClipboardUtil.copyTextToClipboard(
                    context,
                    binding.textMapItemAddress.text.toString(),
                    "주소가 클립보드에 복사되었습니다.")
            }
        }
    }
}