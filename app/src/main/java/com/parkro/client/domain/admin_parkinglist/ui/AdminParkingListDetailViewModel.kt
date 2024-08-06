package com.parkro.client.domain.admin_parkinglist.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.parkro.client.domain.admin_parkinglist.api.GetAdminParkingDetailRes
import com.parkro.client.domain.admin_parkinglist.data.AdminParkingListRepository

class AdminParkingListDetailViewModel: ViewModel() {

    private val adminParkingListRepository = AdminParkingListRepository()

    private val _adminParkingListDetail = MutableLiveData<GetAdminParkingDetailRes>()
    val adminParkingListDetail: LiveData<GetAdminParkingDetailRes> get() = _adminParkingListDetail

    private val _errorState = MutableLiveData<String?>()
    val errorState: LiveData<String?> get() = _errorState

    private val _errorStatePatch = MutableLiveData<String?>()
    val errorStatePatch: LiveData<String?> get() = _errorStatePatch

    fun fetchAdminParkingListDetail(parkingId: Int) {
        adminParkingListRepository.getAdminParkingListDetail(parkingId) { result ->
            result.onSuccess { parkingInfo ->
                _adminParkingListDetail.postValue(parkingInfo)
                Log.d("AdminParkingListDetailViewModel", "주차 정보 상세 조회 완료: $parkingInfo")
            }.onFailure { error ->
                resetParkingListDetail()
                _errorState.postValue(error.message)
            }
        }
    }

    fun fetchAdminParking(parkingId: Int) {
        adminParkingListRepository.patchAdminParkingStatusOut(parkingId) { result ->
            result.onSuccess {
                Log.d("AdminParkingListDetailViewModel", "주차 정보 상태 변경 완료: $parkingId")
            }.onFailure { error ->
                _errorStatePatch.postValue(error.message)
            }
        }
    }

    fun resetParkingListDetail() {
        _adminParkingListDetail.postValue(null)
    }
}