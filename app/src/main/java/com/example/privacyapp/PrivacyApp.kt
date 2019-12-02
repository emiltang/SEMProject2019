package com.example.privacyapp

import android.Manifest
import android.app.Application
import com.example.privacyapp.db.AppDatabase
import com.example.privacyapp.model.PrivacyWarning
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PrivacyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase(this@PrivacyApp).privacyWarningDao().insertAll(
                PrivacyWarning(
                    app = "com.google.android.youtube",
                    permission = Manifest.permission.INTERNET,
                    description = "Youtube can access internet"
                )
            )
        }
    }
}