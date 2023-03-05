package com.example.myapplication.util

import android.util.Log
import java.net.NetworkInterface
import java.net.SocketException

class WifiUtils {
    private val TAG = "WifiUtils"

    fun getDeviceIdAddress(): String {
            var deviceIpAddress = "###.###.###.###"
            try {
                val enumeration = NetworkInterface.getNetworkInterfaces()
                while (enumeration.hasMoreElements()) {
                    val networkInterface = enumeration.nextElement()
                    val enumerationIpAddr = networkInterface.inetAddresses
                    while (enumerationIpAddr.hasMoreElements()) {
                        val inetAddress = enumerationIpAddr.nextElement()
                        if (!inetAddress.isLoopbackAddress && inetAddress.address.size == 4) {
                            deviceIpAddress = inetAddress.hostAddress
                        }
                    }
                }
            } catch (e: SocketException) {
                Log.e(TAG, "SocketException:" + e.message)
            }
            return deviceIpAddress
        }
}
