package com.example.privacyapp.service

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.privacyapp.model.PrivacyWarning
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class UploadWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    private val tag: String = this::class.simpleName.toString()
    private val http = OkHttpClient()
    private val objectMapper = ObjectMapper()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {

            val warning = PrivacyWarning(
                app = inputData.getString("app")!!,
                permission = inputData.getString("permission")!!,
                description = inputData.getString("description")!!
            )
            val json = objectMapper.writeValueAsString(warning).toRequestBody(JSON.toMediaType())

            val request = Request.Builder()
                .url(URL)
                .post(json)
                .build()

            val result = http.newCall(request = request).execute().use { it }

            if (result.isSuccessful) {
                return@withContext Result.success()
            } else {
                Log.e(tag, "body: ${result.body!!.string()} msg: ${result.message} code: ${result.code}")
                return@withContext Result.failure()
            }

        } catch (ex: Exception) {
            Log.e(tag, "Error detected", ex)
            return@withContext Result.failure()
        }
    }

    companion object {
        const val JSON = "application/json; charset=utf-8"
        const val URL = "http://10.0.2.2:8090/warning"
    }
}
