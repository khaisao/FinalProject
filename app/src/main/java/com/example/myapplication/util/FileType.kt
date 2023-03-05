package com.example.myapplication.util

object FileType {
    val FileTypes = arrayOf(
        arrayOf(".png", ".jpg", ".jpeg", ".gif", ".bmp"),
        arrayOf(".mp3", ".wav", ".ogg", "midi"),
        arrayOf(".mp4", ".rmvb", ".avi", ".flv", ".3gp"),
        arrayOf(".jsp", ".html", ".htm", ".js", ".php"),
        arrayOf(".txt", ".c", ".cpp", ".xml", ".py", ".json", ".log"),
        arrayOf(".xls", ".xlsx"),
        arrayOf(".doc", ".docx"),
        arrayOf(".ppt", ".pptx"),
        arrayOf(".pdf"),
        arrayOf(".jar", ".zip", ".rar", ".gz"),
        arrayOf(".apk")
    )
    const val TYPE_IMAGE = 100
    const val TYPE_AUDIO = 200
    const val TYPE_VIDEO = 300
    const val TYPE_WEB = 400
    const val TYPE_TEXT = 500
    const val TYPE_EXCEL = 600
    const val TYPE_WORD = 700
    const val TYPE_PPT = 800
    const val TYPE_PDF = 900
    const val TYPE_PACKAGE = 1000
    const val TYPE_APK = 1100
    const val TYPE_UNKNOWN = -1
    val TypeStart = intArrayOf(
        TYPE_IMAGE, TYPE_AUDIO, TYPE_VIDEO,
        TYPE_WEB, TYPE_TEXT, TYPE_EXCEL, TYPE_WORD, TYPE_PPT, TYPE_PDF, TYPE_PACKAGE, TYPE_APK
    )
}