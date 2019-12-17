package com.example.privacyapp.service

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.privacyapp.db.AppDatabase
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class UploadWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    private val tag = this::class.simpleName
    private val http by lazy { OkHttpClient() }
    private val mapper by lazy { ObjectMapper() }
    private val db by lazy { AppDatabase(context = applicationContext) }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val id = inputData.getLong("id", -1)
            val warning = db.privacyWarningDao()
                .findById(id)
            val json = mapper.writeValueAsString(warning)
                .toRequestBody(JSON.toMediaType())
            val request = Request.Builder()
                .url(URL)
                .post(json)
                .build()
            val result = http.newCall(request)
                .execute()
                .use { it }
            if (result.isSuccessful) {
                Result.success()
            } else {
                Log.e(tag, "Error detected")

                //    Log.e(tag, "body: ${result.body!!.string()} msg: ${result.message} code: ${result.code}")
                Result.failure()
            }
        } catch (ex: Exception) {
            Log.e(tag, "Error detected", ex)
            Result.failure()
        }
    }

    companion object {
        const val JSON = "application/json; charset=utf-8"
        const val URL = "http://10.0.2.2:8090/warning"
    }
}
