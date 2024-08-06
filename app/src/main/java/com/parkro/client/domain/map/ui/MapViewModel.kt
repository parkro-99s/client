package com.parkro.client.domain.map.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parkro.client.domain.map.api.GetParkingLotRes
import com.parkro.client.domain.map.data.ParkingLotRepository
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {

    private val _parkingLots = MutableLiveData<Result<List<GetParkingLotRes>>>()
    val parkingLots: LiveData<Result<List<GetParkingLotRes>>> = _parkingLots

    private val _errorState = MutableLiveData<String?>()
    val errorState: LiveData<String?> = _errorState

    private val repository = ParkingLotRepository()

    // 외부 주차장 목록 조회
    fun fetchParkingLotData(storeId: String) {
        viewModelScope.launch {
            repository.getParkingLotList(storeId) { result ->
                result.onSuccess { parkingLots ->
                    _parkingLots.value = Result.success(parkingLots)
                    _errorState.value = null
                }.onFailure { error ->
                    _parkingLots.value = Result.failure(error)
                    _errorState.value = error.message
                }
            }
        }
    }
}