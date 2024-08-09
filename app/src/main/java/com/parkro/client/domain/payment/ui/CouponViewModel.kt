package com.parkro.client.domain.payment.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.parkro.client.domain.payment.api.GetMemberCouponList
import com.parkro.client.domain.payment.api.GetMemberCouponListItem
import com.parkro.client.domain.payment.data.PaymentRepository

/**
 * 쿠폰 관련 Coupon
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
        _selectedCoupon.value?.let {
            // 이미 선택된 쿠폰이 현재 클릭된 쿠폰과 같으면 해제
            if (it == coupon) {
                _selectedCoupon.postValue(null)
                return
            }
        }
        // 클릭된 쿠폰이 선택된 쿠폰이 아닌 경우
        _selectedCoupon.postValue(coupon)
    }
}