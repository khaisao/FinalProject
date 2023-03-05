package com.example.myapplication.base.dialog

import android.os.Parcel
import android.os.Parcelable

interface OnPositiveDialogListener : Parcelable {
    fun onClick()

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {}
}

interface OnNegativeDialogListener : Parcelable {
    fun onClick()

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {}
}