package com.example.myapplication

import android.graphics.BitmapFactory
import android.os.Build
import android.util.Base64.DEFAULT
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.net.Socket
import java.util.*
import kotlin.math.log
import kotlin.math.min


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

    fun sendMessage(message: String) {
        try {
            val outputStream = socket?.getOutputStream()
            val outputStreamWriter = OutputStreamWriter(outputStream)
            val bufferedWriter = BufferedWriter(outputStreamWriter)

            val chunkSize = 1024 // define chunk size
            var startPos = 0

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

