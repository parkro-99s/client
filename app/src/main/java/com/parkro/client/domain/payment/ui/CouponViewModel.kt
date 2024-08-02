package com.parkro.client.domain.payment.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.parkro.client.domain.payment.api.GetMemberCouponList
import com.parkro.client.domain.payment.api.GetMemberCouponListItem
import com.parkro.client.domain.payment.data.PaymentRepository

class CouponViewModel : ViewModel() {

    private val paymentRepository = PaymentRepository()

    private val _couponList = MutableLiveData<GetMemberCouponList>()
    val couponList: LiveData<GetMemberCouponList> get() = _couponList

    private val _selectedCoupon = MutableLiveData<GetMemberCouponListItem>()
    val selectedCoupon: LiveData<GetMemberCouponListItem> get() = _selectedCoupon

    fun fetchMemberCouponList(username: String) {
        paymentRepository.getMemberCouponList(username) { coupon ->
            coupon.fold(
                onSuccess = { data ->
                    _couponList.postValue(data)
                },
                onFailure = { error ->
                    Log.e("CouponViewModel", "Error fetching coupon list: ${error.message}")
                }
            )
        }
    }

    fun selectCoupon(coupon: GetMemberCouponListItem) {
        _selectedCoupon.postValue(coupon)
    }

    fun useSelectedCoupon() {
        _selectedCoupon.value?.let { coupon ->
            Log.d("CouponViewModel", "쿠폰 사용: $coupon")
            // 서버로 사용된 쿠폰 정보를 전송하는 로직을 추가하세요
            // 예: paymentRepository.useCoupon(coupon)
        }
    }

    fun unselectedCoupon() {
        _selectedCoupon.postValue(null)
    }
}