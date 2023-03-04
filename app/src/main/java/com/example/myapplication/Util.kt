package com.example.myapplication

import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.example.myapplication.ui.HomeActivity
import java.io.File


fun getFileName(absolutePath: String): String {
    return absolutePath.substring(absolutePath.lastIndexOf("/") + 1)
}

fun Context.getAllImagePaths(): ArrayList<String> {
    val allImagePaths = ArrayList<String>()
    val projection = arrayOf(
        MediaStore.Images.Media.DATA
    )
    val selection = "${MediaStore.Images.Media.DATA} like ?"
    val selectionArgs = arrayOf("%")
    val cursor = this.contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        selection,
        selectionArgs,
        null
    )
    cursor?.use {
        val pathColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        while (it.moveToNext()) {
            val path = it.getString(pathColumn)
            if (path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".png") || path.endsWith(".gif")) {
                allImagePaths.add(path)
            }
        }
    }
    return allImagePaths
}

fun Context.getAllAudioPaths(): ArrayList<String> {
    val allAudioPaths = ArrayList<String>()
    val projection = arrayOf(MediaStore.Audio.Media.DATA)
    val selection = "${MediaStore.Audio.Media.DATA} like ?"
    val selectionArgs = arrayOf("%")
    val cursor = this.contentResolver.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        projection,
        selection,
        selectionArgs,
        null
    )

    cursor?.use {
        val pathColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
        while (it.moveToNext()) {
            val path = it.getString(pathColumn)
            val file = File(path)
            if (file.exists() && file.isFile && file.extension.isNotEmpty()) {
                allAudioPaths.add(path)
            }
        }
    }

    return allAudioPaths
}

fun isAudioFile(filePath: String): Boolean {
    return when (filePath.substringAfterLast('.')) {
        "mp3", "wav", "ogg", "m4a", "flac" -> true
        else -> false
    }
}


fun getFileType(path:String): String {
   return path.substring(path.lastIndexOf("."));
}


 var kbToUse = HomeActivity.KB
 var mbToUse = HomeActivity.MB
 var gbToUse = HomeActivity.GB
fun getShiftUnits(x: Long): Pair<Long, String> {
    val usedSpaceUnits: String
    val shift =
        when {
            x < kbToUse -> {
                usedSpaceUnits = "Bytes"; 1L
            }
            x < mbToUse -> {
                usedSpaceUnits = "KB"; kbToUse
            }
            x < gbToUse -> {
                usedSpaceUnits = "MB"; mbToUse
            }
            else -> {
                usedSpaceUnits = "GB"; gbToUse
            }
        }
    return Pair(shift, usedSpaceUnits)
}

fun getAllDocumentFilePaths(context: Context): List<String> {
    val docExtensions = arrayOf(".pdf", ".doc", ".docx", ".txt")

    val filePathList = mutableListOf<String>()

    // Get the external storage directory
    val storageDir = Environment.getExternalStorageDirectory()

    // Recursively traverse the directory
    traverseDirectory(storageDir, filePathList, docExtensions)

    return filePathList
}

fun traverseDirectory(
    dir: File,
    filePathList: MutableList<String>,
    docExtensions: Array<String>
) {
    for (file in dir.listFiles()) {
        if (file.isDirectory) {
            traverseDirectory(file, filePathList, docExtensions)
        } else {
            val extension = getFileExtension(file.name)

            if (docExtensions.contains(extension)) {
                // Add the file path to the list
                filePathList.add(file.absolutePath)
            }
        }
    }
}

fun getFileExtension(filename: String): String {
    val lastDot = filename.lastIndexOf(".")
    return if (lastDot == -1) "" else filename.substring(lastDot)
}

