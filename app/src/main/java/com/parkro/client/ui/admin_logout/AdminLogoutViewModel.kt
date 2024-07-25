package com.parkro.client.ui.admin_logout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AdminLogoutViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is logout Fragment"
    }
    val text: LiveData<String> = _text
}