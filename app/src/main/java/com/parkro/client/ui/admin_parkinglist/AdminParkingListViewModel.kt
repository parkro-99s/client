package com.parkro.client.ui.admin_parkinglist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AdminParkingListViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is admin parking list Fragment"
    }
    val text: LiveData<String> = _text
}