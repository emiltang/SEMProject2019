package com.example.privacyapp.ui

import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver

class AppResultReceiver(private val listener: AppReceiver) : ResultReceiver(Handler()) {

    interface AppReceiver {
        fun onReceiveResult(resultCode: Int, bundle: Bundle)
    }

    override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
        listener.onReceiveResult(resultCode, resultData!!)
    }
}
