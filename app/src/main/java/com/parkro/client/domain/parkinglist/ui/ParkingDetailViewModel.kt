package com.parkro.client.domain.parkinglist.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parkro.client.domain.parkinglist.api.GetParkingDetailRes
import com.parkro.client.domain.parkinglist.data.ParkingRepository
import kotlinx.coroutines.launch

class ParkingDetailViewModel : ViewModel() {
    private val _parkingDetail = MutableLiveData<GetParkingDetailRes>()
    val parkingDetail: LiveData<GetParkingDetailRes> = _parkingDetail

    private val _errorState = MutableLiveData<String?>()
    val errorState: LiveData<String?> = _errorState

    private val _deleteState = MutableLiveData<Boolean>()
    val deleteState: LiveData<Boolean> get() = _deleteState

    private val repository = ParkingRepository()

    // 주차 내역 상세 조회
    fun fetchParkingDetail(parkingId: Int) {
        viewModelScope.launch {
            repository.getParkingDetail(parkingId) { result ->
                result.onSuccess { detail ->
                    _parkingDetail.value = detail
                    _errorState.value = null
                }.onFailure { error ->
                    _errorState.value = error.message
                }
            }
        }
    }

    // 주차 내역 삭제
    fun deleteParkingDetail(parkingId: Int) {
        viewModelScope.launch {
            repository.deleteParkingDetail(parkingId) { result ->
                result.onSuccess { deletedCount ->
                    if (deletedCount > 0) {
                        _deleteState.postValue(true)
                    } else {
                        _errorState.postValue("주차 내역 삭제에 실패했습니다.")
                    }
                }.onFailure { error ->
                    _errorState.value = error.message
                }
            }
        }
    }

}