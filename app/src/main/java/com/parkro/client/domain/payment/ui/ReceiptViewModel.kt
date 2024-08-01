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
                    // ErrorRes 객체 생성
                    _errorMessage.postValue(parseErrorResponse(throwable))
                }
            )
        }
    }

    private fun parseErrorResponse(throwable: Throwable): ErrorRes {
        // 서버에서 반환하는 에러 JSON을 파싱하여 ErrorRes 객체로 변환하는 로직
        // 예를 들어, HttpException 등을 사용할 수 있음
        // 이 예제는 가상의 로직입니다. 실제 구현은 서버 응답 형식에 맞춰야 함
        val errorJson = throwable.localizedMessage // 또는 다른 방법으로 JSON을 추출
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
}
