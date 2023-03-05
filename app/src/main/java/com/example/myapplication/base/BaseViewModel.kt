package com.example.myapplication.base

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import org.json.JSONArray
import org.json.JSONObject

abstract class BaseViewModel : ViewModel() {
    var isLoading = MutableLiveData(false)
    val _activity = MutableLiveData<Activity>()
    fun setActivityViewModel(activity: Activity) {
        _activity.value = activity
    }

    open fun clearAllApi() {
        viewModelScope.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        clearAllApi()
    }
}
