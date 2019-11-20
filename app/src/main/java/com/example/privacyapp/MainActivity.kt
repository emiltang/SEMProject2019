package com.example.privacyapp

import android.Manifest.permission.READ_PHONE_STATE
import android.content.Intent
import android.os.Bundle
import android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS
import android.view.View
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private val tag = this::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startActivity(Intent(ACTION_USAGE_ACCESS_SETTINGS))
        requestPermissions(arrayOf(READ_PHONE_STATE), 0)
    }

    @Suppress("UNUSED_PARAMETER")
    fun button(view: View) {
        val ids = packageManager.getInstalledApplications(0).map { it.uid }
        val intent = Intent(this, NetworkUsageService::class.java)
        intent.putExtra("uids", ids.toIntArray())
        NetworkUsageService.enqueueWork(this, intent)
    }
}
