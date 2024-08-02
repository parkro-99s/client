package com.parkro.client.domain.payment.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.parkro.client.common.data.ErrorRes
import com.parkro.client.domain.payment.api.GetReceiptRes
import com.parkro.client.domain.payment.data.ReceiptRepository
import org.json.JSONException
import org.json.JSONObject

class ReceiptViewModel : ViewModel() {

    private val receiptRepository = ReceiptRepository()

    private val _receiptData = MutableLiveData<GetReceiptRes>()
    val receiptData: LiveData<GetReceiptRes> get() = _receiptData

    private val _errorMessage = MutableLiveData<ErrorRes>()
    val errorMessage: LiveData<ErrorRes> get() = _errorMessage

    fun resetReceiptData() {
        _receiptData.value = null
        _errorMessage.value = null
    }

    fun fetchMemberReceiptInfo(receiptId: Int) {
        receiptRepository.getMemberReceiptInfo(receiptId) { result ->
            result.fold(
                onSuccess = { data ->
                    _receiptData.postValue(data)
                },
                onFailure = { throwable ->
                    _errorMessage.postValue(parseErrorResponse(throwable))
                }
            )
        }
    }

    private fun parseErrorResponse(throwable: Throwable): ErrorRes {
        val errorJson = throwable.localizedMessage
        return try {
            val jsonObject = JSONObject(errorJson)
            ErrorRes(
                status = jsonObject.getInt("status"),
                errorCode = jsonObject.getString("errorCode"),
                message = jsonObject.getString("message")
            )
        } catch (e: JSONException) {
            ErrorRes(
                status = 500,
                errorCode = "UNKNOWN_ERROR",
                message = "An unknown error occurred"
            )
        }
    }

    fun calculateDiscountTime(totalPrice: Int): Int {
        return when {
            totalPrice >= 60000 -> 5
            totalPrice >= 40000 -> 3
            totalPrice >= 30000 -> 2
            totalPrice >= 20000 -> 1
            else -> 0
        }
    }
}
