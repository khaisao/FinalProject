package com.example.myapplication

import android.os.Environment
import java.io.File

object Constants {
    const val HTTP_PORT = 12345
    const val DIR_IN_SDCARD = "TLUTransfer"
    const val MSG_DIALOG_DISMISS = 0
    val DIR =
        File(Environment.getExternalStorageDirectory().toString() + File.separator + DIR_IN_SDCARD)
    val DIR_PATH = DIR.absolutePath

    object RxBusEventType {
        const val POPUP_MENU_DIALOG_SHOW_DISMISS = "POPUP MENU DIALOG SHOW " +
                "DISMISS"
        const val WIFI_CONNECT_CHANGE_EVENT = "WIFI CONNECT CHANGE EVENT"
        const val LOAD_BOOK_LIST = "LOAD BOOK LIST"
    }
}