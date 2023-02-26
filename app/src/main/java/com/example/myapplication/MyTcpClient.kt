package com.example.myapplication

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.net.Socket


class MyTcpClient {
    private var socket: Socket? = null
    private var out: PrintWriter? = null
    private var input: BufferedReader? = null

    suspend fun connect(): String = withContext(Dispatchers.IO) {
        try {
            // Connect to server
            socket = Socket("10.0.2.2", 8080)

            // Initialize output and input streams
            out = PrintWriter(BufferedWriter(OutputStreamWriter(socket!!.getOutputStream())), true)
            input = BufferedReader(InputStreamReader(socket!!.getInputStream()))

            // Send message to server
//            out!!.println("Hello from Android client!")

            // Receive response from server

            return@withContext "response"
        } catch (e: IOException) {
            throw e
        }
    }

    fun sendFile(path:String) {
        try {
            val outputStream = socket?.getOutputStream()
            val outputStreamWriter = OutputStreamWriter(outputStream)
            val bufferedWriter = BufferedWriter(outputStreamWriter)
            val fileName = getFileName(path)
            var base64String = ""
            if(getFileType(path) == ".png" || getFileType(path)==".jpg" || getFileType(path) ==".jpeg"){
                 base64String = getBase64OfImageFile(path)
            }
            if(getFileType(path) == ".txt" ){
                base64String = getBase64OfTxtFile(path)
            }
            val message = "$fileName,$base64String"
            val chunkSize = 1024 // define chunk size
            var startPos = 0

            // Send the message in chunks
            while (startPos < message.length) {
                val endPos = minOf(startPos + chunkSize, message.length)
                val chunk = message.substring(startPos, endPos)
                bufferedWriter.write(chunk)
                startPos += chunkSize
            }

            // Add a null terminator at the end of the message
            bufferedWriter.write(0)

            bufferedWriter.flush()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("Error", "Error send file: $e")
        }
    }





    fun disconnect() {
        socket?.let {
            try {
                it.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}

