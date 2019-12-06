package com.example.privacyapp.service

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.privacyapp.db.AppDatabase
import com.example.privacyapp.model.PrivacyWarning
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.coroutines.suspendCoroutine

class SyncWorker(context: Context, workerParameters: WorkerParameters) : CoroutineWorker(context, workerParameters) {


    private val tag: String = this::class.simpleName.toString()
    private val http = OkHttpClient()
    private val mapper = ObjectMapper()


    private suspend fun getList() = suspendCoroutine<List<PrivacyWarning>> {
        val request = Request.Builder().url(URL).get().build()

        val result = http.newCall(request = request).execute().use {
            it.body!!.toString()
        }
        mapper.readValue(result, object : TypeReference<List<PrivacyWarning>>() {})
    }

    override suspend fun doWork() = withContext(Dispatchers.IO) {
        try {
            val list = getList()
            val dao = AppDatabase(context = applicationContext).privacyWarningDao()
            dao.insertAll(list)
            Result.success()
        } catch (ex: Exception) {
            Log.d(tag, "Http get error", ex)
            Result.failure()
        }
    }

    companion object {
        const val URL = "http://10.0.2.2:8090/warning"
    }
}