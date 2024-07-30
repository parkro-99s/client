package com.parkro.client.domain.example.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.parkro.client.domain.example.api.ExampleDTO
import com.parkro.client.domain.example.data.ExampleRepository

class ExampleViewModel : ViewModel() {

    private val exampleRepository = ExampleRepository()

    /** LiveData
     * Data의 변경을 관찰 할 수 있는 DataHolder
     * UI에 실시간으로 데이터를 뿌려주기에 적합
     */
    private val _exampleResult = MutableLiveData<ExampleDTO>()

    // 외부에서 접근 가능한 LiveData
    val exampleResult: LiveData<ExampleDTO> get() = _exampleResult

    // repository 에서 받은 응답 데이터를 가져오는 메서드
    fun fetchMemberReceiptInfo(receiptId: Int) {
        // API 호출 성공/실패 여부에 따라 넘겨줄 지정
        exampleRepository.getMemberReceiptInfo(receiptId) { result ->
            result.fold(
                onSuccess = { data -> _exampleResult.postValue(data) },
                onFailure = { throwable ->
                    _exampleResult.postValue(null)
                }
            )
        }
    }
}
