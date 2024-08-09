package com.parkro.client.domain.map.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parkro.client.domain.map.api.GetParkingLotRes
import com.parkro.client.domain.map.data.ParkingLotRepository
import kotlinx.coroutines.launch

/**
 * 지도 뷰 모델
 *
 * @author 김민정
 * @since 2024.07.31
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.08.01   김민정       최초 생성
 * </pre>
 */
class MapViewModel : ViewModel() {

    // 주차장 리스트
    private val _parkingLots = MutableLiveData<Result<List<GetParkingLotRes>>>()
    val parkingLots: LiveData<Result<List<GetParkingLotRes>>> = _parkingLots

    // 에러 상태
    private val _errorState = MutableLiveData<String?>()
    val errorState: LiveData<String?> = _errorState

    private val repository = ParkingLotRepository()

    // 외부 주차장 목록 조회
    fun fetchParkingLotData(storeId: String) {
        viewModelScope.launch {
            repository.getParkingLotList(storeId) { result ->
                // 데이터 요청 성공
                result.onSuccess { parkingLots ->
                    _parkingLots.value = Result.success(parkingLots)
                    _errorState.value = null
                }.onFailure { error ->
                    // 데이터 요청 실패
                    _parkingLots.value = Result.failure(error)
                    _errorState.value = error.message
                }
            }
        }
    }
}