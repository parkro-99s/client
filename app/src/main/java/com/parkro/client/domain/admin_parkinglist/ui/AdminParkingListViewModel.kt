package com.parkro.client.domain.admin_parkinglist.ui

import android.util.Log
import androidx.lifecycle.*
import com.parkro.client.domain.admin_parkinglist.api.GetAdminParkingRes
import com.parkro.client.domain.admin_parkinglist.data.AdminParkingListRepository
import kotlinx.coroutines.launch

class AdminParkingListViewModel : ViewModel() {

    private val adminParkingListRepository = AdminParkingListRepository()

    private val _adminParkingList = MutableLiveData<List<GetAdminParkingRes>>()
    val adminParkingList: LiveData<List<GetAdminParkingRes>> get() = _adminParkingList

    private val _selectedYear = MutableLiveData<Int>()
    val selectedYear: LiveData<Int> get() = _selectedYear

    private val _selectedMonth = MutableLiveData<Int>()
    val selectedMonth: LiveData<Int> get() = _selectedMonth

    private val _selectedDay = MutableLiveData<Int>()
    val selectedDay: LiveData<Int> get() = _selectedDay

    private val _selectedCarNumber = MutableLiveData<String>()
    val selectedCarNumber: LiveData<String> get() = _selectedCarNumber

    private val _selectedStore = MutableLiveData<Int>()
    val selectedStore: LiveData<Int> get() = _selectedStore

    private val _selectedParkingLot = MutableLiveData<Int>()
    val selectedParkingLot: LiveData<Int> get() = _selectedParkingLot

    private val _errorState = MutableLiveData<String?>()
    val errorState: LiveData<String?> get() = _errorState

    private var currentPage = 1
    private var isLoading = false
    private var hasMoreData = true

    fun fetchAdminParkingList(storeId: Int, parkingLotId: Int, date: String) {
        if (isLoading || !hasMoreData) return
        isLoading = true

        Log.d("AdminParkingListviewModel", "fetch parking list admin: $date")

        viewModelScope.launch {
            adminParkingListRepository.getAdminParkingList(storeId, parkingLotId, date, _selectedCarNumber.value, currentPage) { result ->
                result.onSuccess { data ->
                    val currentList = _adminParkingList.value.orEmpty().toMutableList()
                    Log.d("AdminParkingListviewModel", "currentList: $currentList")
                    currentList.addAll(data)
                    _adminParkingList.postValue(currentList)
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

    fun getSelectedYear(): Int? {
        return _selectedYear.value
    }

    fun getSelectedMonth(): Int? {
        return _selectedMonth.value
    }

    fun getSelectedDay(): Int? {
        return _selectedDay.value
    }

    fun updateSelectedDate(year: Int, month: Int, day: Int) {
        _selectedYear.postValue(year)
        _selectedMonth.postValue(month)
        _selectedDay.postValue(day)
        Log.d("AdminParkingListviewModel", "update selected date at view model: $year $month $day")
    }

    fun updateSelectedCarNumber(car: String) {
        _selectedCarNumber.postValue(car)
        Log.d("AdminParkingListviewModel", "update selected car at view model: $car")
    }

    fun updateSelectedStore(storeId: Int) {
        _selectedStore.postValue(storeId)
        Log.d("AdminParkingListviewModel", "update selected store at view model: $storeId")
    }

    fun updateSelectedParkingLot(parkingLotId: Int) {
        _selectedParkingLot.postValue(parkingLotId)
        Log.d("AdminParkingListviewModel", "update selected parkingLot at view model: $parkingLotId")
    }

    fun resetData() {
        currentPage = 1
        hasMoreData = true
        _adminParkingList.value = emptyList()
    }
}
