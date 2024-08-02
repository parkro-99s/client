package com.parkro.client.domain.payment.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.parkro.client.R
import com.parkro.client.databinding.ItemCouponBinding
import com.parkro.client.domain.payment.api.GetMemberCouponListItem
import java.text.SimpleDateFormat
import java.util.*

class CouponRecyclerAdapter(
    private var couponList: List<GetMemberCouponListItem>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<CouponRecyclerAdapter.CouponViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(coupon: GetMemberCouponListItem, position: Int)
    }

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCouponBinding.inflate(inflater, parent, false)
        return CouponViewHolder(binding, onItemClickListener)
    }

    override fun onBindViewHolder(holder: CouponViewHolder, position: Int) {
        holder.bind(couponList[position], position == selectedPosition)
    }

    override fun getItemCount(): Int = couponList.size

    inner class CouponViewHolder(
        private val binding: ItemCouponBinding,
        private val onItemClickListener: OnItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(coupon: GetMemberCouponListItem, isSelected: Boolean) {
            val context = itemView.context
            binding.discountHour.text = context.getString(R.string.formatted_free_coupon_hour, coupon.discountHour)
            // 포맷된 날짜 문자열 생성
            val originalFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val targetFormat = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())

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

            itemView.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
                onItemClickListener.onItemClick(coupon, selectedPosition)
            }
        }
    }
}
