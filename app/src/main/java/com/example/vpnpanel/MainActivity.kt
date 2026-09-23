package com.example.vpnpanel

import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.vpnpanel.service.GameVpnService

class MainActivity : AppCompatActivity() {

    private val vpnLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            startVpnService()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnToggleVpn = findViewById<Button>(R.id.btnToggleVpn)
        btnToggleVpn.setOnClickListener {
            prepareAndStartVpn()
        }
    }

    private fun prepareAndStartVpn() {
        val intent = VpnService.prepare(this)
        if (intent != null) {
            vpnLauncher.launch(intent)
        } else {
            startVpnService()
        }
    }

    private fun startVpnService() {
        val intent = Intent(this, GameVpnService::class.java)
        startService(intent)
    }
}
