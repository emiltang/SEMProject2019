package com.example.privacyapp.service

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import okhttp3.OkHttpClient
import okhttp3.Request


class MyAlarmManager(context: Context, parameters: WorkerParameters) : Worker(context, parameters) {

    private val httpClient = OkHttpClient()

    override fun doWork(): Result {
        val request = Request.Builder().url("").build()

        httpClient.newCall(request).execute().use {
            val body = it.body!!.string()
        }


        return Result.success()
    }
}