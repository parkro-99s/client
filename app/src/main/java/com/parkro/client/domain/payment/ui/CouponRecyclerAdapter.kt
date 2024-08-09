package com.parkro.client.domain.payment.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.parkro.client.R
import com.parkro.client.databinding.ItemCouponBinding
import com.parkro.client.domain.payment.api.GetMemberCouponListItem
import java.text.SimpleDateFormat
import java.util.*

/**
 * 보유중인 쿠폰 목록 RecyclerView
 *
 * @author 김지수
 * @since 2024.08.02
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.08.02   김지수      최초 생성
 * </pre>
 */
class CouponRecyclerAdapter(
    private var couponList: List<GetMemberCouponListItem>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<CouponRecyclerAdapter.CouponViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(coupon: GetMemberCouponListItem, position: Int)
    }

    // 현재 선택된 쿠폰 관리
    private var selectedCoupon: GetMemberCouponListItem? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCouponBinding.inflate(inflater, parent, false)
        return CouponViewHolder(binding, onItemClickListener)
    }

    override fun onBindViewHolder(holder: CouponViewHolder, position: Int) {
        holder.bind(couponList[position], couponList[position] == selectedCoupon)
    }

    override fun getItemCount(): Int = couponList.size

    // 쿠폰 리스트 변경
    fun updateCouponList(newCouponList: List<GetMemberCouponListItem>) {
        couponList = newCouponList
        notifyDataSetChanged()
    }

    // 선택된 쿠폰 변경
    fun updateSelectedCoupon(newSelectedCoupon: GetMemberCouponListItem?) {
        val previousSelectedCoupon = selectedCoupon
        selectedCoupon = newSelectedCoupon
        val previousPosition = couponList.indexOf(previousSelectedCoupon)
        val newPosition = couponList.indexOf(selectedCoupon)

        if (previousPosition != RecyclerView.NO_POSITION) {
            notifyItemChanged(previousPosition)
        }
        if (newPosition != RecyclerView.NO_POSITION) {
            notifyItemChanged(newPosition)
        }
    }

    inner class CouponViewHolder(
        private val binding: ItemCouponBinding,
        private val onItemClickListener: OnItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(coupon: GetMemberCouponListItem, isSelected: Boolean) {
            val context = itemView.context
            binding.discountHour.text = context.getString(R.string.formatted_free_coupon_hour, coupon.discountHour)
            val originalFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val targetFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
            binding.endDate.text = "유효 기간: ${targetFormat.format(originalFormat.parse(coupon.endDate))}"

            if (isSelected) {
                binding.discountHour.setTextColor(ContextCompat.getColor(context, R.color.parkro_white))
                binding.endDate.setTextColor(ContextCompat.getColor(context, R.color.parkro_white))
                binding.imgCouponCardBackground.setImageResource(R.drawable.card_selected_coupon)
            } else {
                binding.discountHour.setTextColor(ContextCompat.getColor(context, R.color.default_black))
                binding.endDate.setTextColor(ContextCompat.getColor(context, R.color.default_black))
                binding.imgCouponCardBackground.setImageResource(R.drawable.card_blank_coupon)
            }

            // 아이템 선택했을 때
            itemView.setOnClickListener {
                val clickedCoupon = couponList[adapterPosition]
                val newSelectedCoupon = if (selectedCoupon == clickedCoupon) null else clickedCoupon
                updateSelectedCoupon(newSelectedCoupon)
                onItemClickListener.onItemClick(clickedCoupon, adapterPosition)
            }
        }
    }
}
