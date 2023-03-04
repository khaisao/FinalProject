package com.example.myapplication.data

import android.os.storage.StorageVolume

data class VolumeStats(
    val mStorageVolume: StorageVolume,
    var mTotalSpace: Long = 0,
    var mUsedSpace: Long = 0
)
