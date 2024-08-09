package com.parkro.client.domain.parkinglist.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parkro.client.common.data.ErrorResponseException
import com.parkro.client.domain.parkinglist.api.GetParkingRes
import com.parkro.client.domain.parkinglist.data.ParkingRepository
import kotlinx.coroutines.launch

/**
 * 주차 내역 뷰 모델
 *
 * @author 김민정
 * @since 2024.08.03
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.08.03   김민정       최초 생성
 * </pre>
 */
class ParkingListViewModel() : ViewModel() {

    // 주차 리스트 데이터 저장
    private val _parkingList = MutableLiveData<List<GetParkingRes>>()
    val parkingList: LiveData<List<GetParkingRes>> = _parkingList

    // 에러 상태 저장
    private val _errorState = MutableLiveData<Int?>()
    val errorState: LiveData<Int?> = _errorState

    private val repository = ParkingRepository()
    private var currentPage = 1     // 현재 페이지
    private var isLoading = false   // 데이터 로딩 상태
    private var hasMoreData = true  // 추가 데이터 여부

    // 주차 리스트 데이터 조회
    fun fetchParkingList(username: String) {
        // 데이터 로딩 중 or 추가 데이터 없을 시 종료
        if (isLoading || !hasMoreData) return

        isLoading = true
        viewModelScope.launch {
            repository.getParkingList(username, currentPage) { result ->

                // 데이터 요청 성공
                result.onSuccess { list ->
                    // 기존 리스트에 새로운 데이터 추가
                    val currentList = _parkingList.value.orEmpty().toMutableList()
                    Log.d("AdminParkingListviewModel", "currentList: $currentList")
                    currentList.addAll(list)
                    _parkingList.postValue(currentList)
                    _errorState.postValue(null)
                    currentPage++
                    isLoading = false

                    // 데이터가 비었다면 추가 데이터가 없다고 설정
                    if (list.isEmpty()) {
                        hasMoreData = false
                    }

                }.onFailure { error ->
                    // 데이터 요청 실패
                    val errorCode = when (error) {
                        is ErrorResponseException -> if (currentPage == 1) error.errorRes.status else null
                        else -> 500
                    }

                    errorCode?.let {
                        _errorState.value = it
                    }
                    hasMoreData = false
                    isLoading = false
                }
            }
        }
    }
}