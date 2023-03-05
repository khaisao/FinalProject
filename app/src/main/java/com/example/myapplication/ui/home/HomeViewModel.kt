package com.example.myapplication.ui.home

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.myapplication.Constants
import com.example.myapplication.base.BaseViewModel
import com.example.myapplication.data.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val application: Application
):BaseViewModel() {
    val listFileSent: Flow<UiState<List<String>>>
        get() = _listFileSent
    private val _listFileSent = MutableStateFlow<UiState<List<String>>>(UiState.Loading)
    init {
        getAllImages()
    }
    private fun getAllImages(){
        viewModelScope.launch {
            try {
                val listFile = arrayListOf<String>()
                val directory = Constants.DIR
                val files = directory.listFiles()

                if (files != null) {
                    for (file in files) {
                        val filePath = file.absolutePath
                        listFile.add(filePath)
                    }
                }
                _listFileSent.value = UiState.Success(listFile)
            } catch (e: Exception) {
                _listFileSent.value = UiState.Failure(e.message)
            }
        }
    }

}