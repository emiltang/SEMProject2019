package com.example.privacyapp

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    /* https://www.thegeekstuff.com/2014/06/android-vpn-service */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClickStart(view: View) {
        val vpnService = VpnService.prepare(this)
        if (vpnService != null) {
            startActivityForResult(vpnService, 0)
        } else {
            onActivityResult(0, Activity.RESULT_OK, null)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val vpnService = Intent(this, NetworkAnalyzerVpnService::class.java)
            startService(vpnService)
        }
    }

}
