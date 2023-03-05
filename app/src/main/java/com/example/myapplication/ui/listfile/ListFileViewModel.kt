package com.example.myapplication.ui.listfile

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.myapplication.Constants
import com.example.myapplication.base.BaseViewModel
import com.example.myapplication.getAllAudioPaths
import com.example.myapplication.getAllDocumentFilePaths
import com.example.myapplication.getAllImagePaths
import com.example.myapplication.data.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ListFileViewModel @Inject constructor(
    private val application: Application
):BaseViewModel() {
    private val _listFile = MutableStateFlow<UiState<List<String>?>> (UiState.Loading)
    val listFile: Flow<UiState<List<String>?>>
        get() = _listFile

    fun getAllFile(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val allImagePaths = withContext(Dispatchers.IO) {
                    application.getAllImagePaths() + application.getAllAudioPaths() + getAllDocumentFilePaths(application)
                }
                _listFile.value = UiState.Success(allImagePaths)
            } catch (e: Exception) {
                _listFile.value = UiState.Failure(e.message)
            }
        }
    }

    fun getAllImages(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val allImagePaths = withContext(Dispatchers.IO) {
                    application.getAllImagePaths()
                }
                _listFile.value = UiState.Success(allImagePaths)
            } catch (e: Exception) {
                _listFile.value = UiState.Failure(e.message)
            }
        }
    }

    fun getAllAudio(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val allImagePaths = withContext(Dispatchers.IO) {
                    application.getAllAudioPaths()
                }
                _listFile.value = UiState.Success(allImagePaths)
            } catch (e: Exception) {
                _listFile.value = UiState.Failure(e.message)
            }
        }
    }
    fun getAllDocument(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val allImagePaths = withContext(Dispatchers.IO) {
                    getAllDocumentFilePaths(application)
                }
                _listFile.value = UiState.Success(allImagePaths)
            } catch (e: Exception) {
                _listFile.value = UiState.Failure(e.message)
            }
        }
    }

     fun getAllRecentFile(){
        viewModelScope.launch(Dispatchers.IO) {
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
                _listFile.value = UiState.Success(listFile)
            } catch (e: Exception) {
                _listFile.value = UiState.Failure(e.message)
            }
        }
    }

}