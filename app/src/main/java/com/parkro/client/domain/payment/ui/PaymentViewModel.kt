package com.parkro.client.domain.payment.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.parkro.client.domain.payment.api.GetCurrentParkingInfo
import com.parkro.client.domain.payment.data.PaymentRepository
import kotlin.math.max

/**
 * 정산 ViewModel
 *
 * @author 김지수
 * @since 2024.08.02
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.08.02   김지수      최초 생성
 * 2024.08.02   김지수      정산 시 이루어지는 할인, 주차 정보 등 데이터 관레
 * 2024.08.02   김지수      영수증 등록 내역 반영
 * </pre>
 */
class PaymentViewModel : ViewModel() {

    private val paymentRepository = PaymentRepository()

    // 주차 정보
    private val _parkingInfoRes = MutableLiveData<GetCurrentParkingInfo>()
    val currentParkingInfo: LiveData<GetCurrentParkingInfo> get() = _parkingInfoRes

    // 영수증 할인 시간
    private val _discountReceiptHours = MutableLiveData<Int>()
    val discountReceiptHours: LiveData<Int> get() = _discountReceiptHours

    // 쿠폰 할인 시간
    private val _discountCouponHours = MutableLiveData<Int>()
    val discountCouponHours: LiveData<Int> get() = _discountCouponHours

    // 정산 필요 시간
    private val _totalTimeToPay = MutableLiveData<Int>()
    val totalTimeToPay: LiveData<Int> get() = _totalTimeToPay

    // 정산 금액
    private val _totalAmountToPay = MutableLiveData<Int>()
    val totalAmountToPay: LiveData<Int> get() = _totalAmountToPay

    // 분 단위로 변경
    private fun calculateTotalMinutes(hours: Int?, minutes: Int?): Int {
        return (hours ?: 0) * 60 + (minutes ?: 0)
    }

    fun calculatePaymentTotalTime() {
        val parkingInfo = currentParkingInfo.value
        val discountReceiptHours = discountReceiptHours.value ?: 0
        val discountCouponHours = discountCouponHours.value ?: 0

        _totalTimeToPay.postValue(parkingInfo?.let {
            val totalMinutes = calculateTotalMinutes(it.parkingTimeHour, it.parkingTimeMinute)
            val discountMinutes = (discountCouponHours + discountReceiptHours) * 60
            max(0, totalMinutes - discountMinutes)
        } ?: 0)
    }

    fun calculateAmountToPay() {
        val totalMinutes = totalTimeToPay.value ?: 0
        val pricePerUnit = currentParkingInfo.value?.perPrice ?: 0

        if (totalMinutes > 0 && pricePerUnit > 0) {
            _totalAmountToPay.postValue((totalMinutes / 10) * pricePerUnit)
        } else {
            _totalAmountToPay.postValue(0)
        }
    }

    // 영수증 할인 데이터 등록되면 호출되는 메서드
    fun setDiscountReceiptHours(discountHours: Int) {
        _discountReceiptHours.postValue(discountHours)
        calculatePaymentTotalTime()
        calculateAmountToPay()
    }

    fun setDiscountCouponHours(discountHours: Int) {
        _discountCouponHours.postValue(discountHours)
        calculatePaymentTotalTime()
        calculateAmountToPay()
    }

    fun fetchParkingInfo(username: String) {
        paymentRepository.findParkingInfoFirst(username) { result ->
            result.onSuccess { parkingInfo ->
                _parkingInfoRes.postValue(parkingInfo)
            }.onFailure {
                resetAllData()
            }
        }
    }

    fun resetDiscounted() {
        _discountReceiptHours.postValue(0)
        _discountCouponHours.postValue(0)
    }

    fun resetDiscountReceipt() {
        _discountReceiptHours.postValue(0)
    }

    fun resetAllData() {
        _parkingInfoRes.postValue(null)
        _discountReceiptHours.postValue(0)
        _discountCouponHours.postValue(0)
        _totalTimeToPay.postValue(0)
        _totalAmountToPay.postValue(0)
    }
}
