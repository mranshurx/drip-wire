package com.example.vpnpanel.service

import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import android.util.Log
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class GameVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private var isRunning = false
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    companion object {
        private const val TAG = "GameVpnService"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isRunning) {
            isRunning = true
            startVpn()
        }
        return START_STICKY
    }

    private fun startVpn() {
        val builder = Builder()
            .setSession("FreeFireVpnPanel")
            .addAddress("10.0.0.2", 24) // Local TUN interface IP
            .addRoute("0.0.0.0", 0)   // Route all traffic through the tunnel
            .addDnsServer("8.8.8.8")

        try {
            vpnInterface = builder.establish()
            startPacketProcessing()
            Log.i(TAG, "VPN Interface established successfully.")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to establish VPN interface", e)
            stopVpn()
        }
    }

    private fun startPacketProcessing() {
        val descriptor = vpnInterface ?: return
        executor.submit {
            try {
                val inputStream = FileInputStream(descriptor.fileDescriptor)
                val outputStream = FileOutputStream(descriptor.fileDescriptor)
                val buffer = ByteBuffer.allocate(32767)

                while (isRunning) {
                    val length = inputStream.read(buffer.array())
                    if (length > 0) {
                        // Raw IP packet captured in buffer.array() from index 0 to length
                        // TODO: Parse IP/UDP headers and filter game traffic here
                        
                        // Write packet back to maintain socket loop (dummy echo)
                        outputStream.write(buffer.array(), 0, length)
                    }
                    buffer.clear()
                }
            } catch (e: IOException) {
                Log.e(TAG, "Error processing packets", e)
            }
        }
    }

    private fun stopVpn() {
        isRunning = false
        try {
            vpnInterface?.close()
            vpnInterface = null
        } catch (e: IOException) {
            Log.e(TAG, "Error closing VPN interface", e)
        }
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopVpn()
    }
}
