package com.parkro.client.domain.payment.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.parkro.client.domain.payment.api.GetCurrentParkingInfo
import com.parkro.client.domain.payment.data.PaymentRepository

class PaymentViewModel : ViewModel() {

    private val paymentRepository = PaymentRepository()

    private val _parkingInfoRes = MutableLiveData<GetCurrentParkingInfo>()
    val currentParkingInfo: LiveData<GetCurrentParkingInfo> get() = _parkingInfoRes
    fun fetchParkingInfo(username: String) {
        paymentRepository.findParkingInfoFirst(username) { result ->
            result.onSuccess {
                _parkingInfoRes.postValue(it) // 변경
            }.onFailure {
                _parkingInfoRes.postValue(null) // 변경
            }
        }
    }
}
