package com.parkro.client.domain.admin_parkinglist.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parkro.client.domain.admin_parkinglist.api.GetAdminParkingRes
import com.parkro.client.domain.admin_parkinglist.data.AdminParkingListRepository
import kotlinx.coroutines.launch

class AdminParkingListViewModel : ViewModel() {

    private val adminParkingListRepository = AdminParkingListRepository()

    private val _adminParkingList = MutableLiveData<List<GetAdminParkingRes>>()
    val adminParkingList: LiveData<List<GetAdminParkingRes>> get() = _adminParkingList

    private val _adminParkingListSelectedParkingId = MutableLiveData<Int>()
    val adminParkingListSelectedParkingId: LiveData<Int> get() = _adminParkingListSelectedParkingId

    private val _errorState = MutableLiveData<String?>()
    val errorState: LiveData<String?> get() = _errorState

    private var currentPage = 1
    private var isLoading = false
    private var hasMoreData = true

    fun fetchAdminParkingList(storeId: Int, date: String, car: String? = null) {
        if (isLoading || !hasMoreData) return
        isLoading = true

        viewModelScope.launch {
            adminParkingListRepository.getAdminParkingList(storeId, date, car, currentPage) { result ->
                result.onSuccess { data ->
                    val currentList = _adminParkingList.value.orEmpty().toMutableList()
                    currentList.addAll(data)
                    Log.d("AdminParkingListViewModel", "response data $data")
                    _adminParkingList.postValue(currentList)
                    Log.d("AdminParkingListViewModel", "admin parking list ${_adminParkingList.value}")
                    _errorState.postValue(null)
                    isLoading = false

                    if (data.isNotEmpty()) {
                        currentPage++
                    }
                    hasMoreData = data.size >= 10

                }.onFailure { error ->
                    _errorState.postValue(error.message)
                    hasMoreData = false
                    isLoading = false
                }
            }
        }
    }

    fun updateSelectedParkingDetail(parkingId: Int) {
        _adminParkingListSelectedParkingId.postValue(parkingId)
        Log.d("AdminParkingListViewModel", "parkingId: $parkingId")
    }

    fun resetSelectedParkingDetail() {
        _adminParkingListSelectedParkingId.postValue(0)
    }

    fun resetData() {
        currentPage = 1
        hasMoreData = true
        _adminParkingList.value = emptyList()
    }
}
