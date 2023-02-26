package com.example.myapplication

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream


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
            allImagePaths.add(path)
        }
    }
    return allImagePaths
}

fun getBase64OfImageFile(path: String): String {
    val file = File(path)
    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
    // initialize byte stream
    val stream = ByteArrayOutputStream()
    // compress Bitmap
    bitmap.compress(Bitmap.CompressFormat.JPEG, 1, stream)
    // Initialize byte array
    val byteArray = stream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

fun getBase64OfTxtFile(path: String): String {
    val file = File(path)
    val inputStream = FileInputStream(file)
    val buffer = ByteArray(file.length().toInt())
    inputStream.read(buffer)
    inputStream.close()
    return Base64.encodeToString(buffer, Base64.DEFAULT)
}

fun getFileType(path:String): String {
   return path.substring(path.lastIndexOf("."));
}

