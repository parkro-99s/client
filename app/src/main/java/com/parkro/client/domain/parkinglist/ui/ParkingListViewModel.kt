package com.parkro.client.domain.parkinglist.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ParkingListViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is parkinglist Fragment"
    }
    val text: LiveData<String> = _text
}