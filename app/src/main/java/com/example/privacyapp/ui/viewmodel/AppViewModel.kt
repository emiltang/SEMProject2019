package com.example.privacyapp.ui.viewmodel

import android.app.Application
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.privacyapp.db.AppDatabase
import com.example.privacyapp.model.NetworkActivityRecord
import com.example.privacyapp.service.NetworkUsageService
import com.example.privacyapp.ui.AppResultReceiver

class AppViewModel(
    application: Application,
    private val applicationInfo: ApplicationInfo
) : AndroidViewModel(application), AppResultReceiver.AppReceiver, IWarningViewModel {

    private val tag = this::class.simpleName
    private val db by lazy { AppDatabase(getApplication()) }

    val netData: MutableLiveData<List<NetworkActivityRecord>> get() = _netData
    private val _netData by lazy { MutableLiveData<List<NetworkActivityRecord>>().also { requestNetData() } }

    override val warnings by lazy { db.privacyWarningDao().findByAppName(applicationInfo.packageName) }

    private fun requestNetData() {
        val intent = Intent(getApplication(), NetworkUsageService::class.java).apply {
            putExtra("resultReceiver", AppResultReceiver(this@AppViewModel))
            putExtra("uid", applicationInfo.uid)
        }
        NetworkUsageService.enqueueWork(getApplication(), intent)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onReceiveResult(resultCode: Int, bundle: Bundle) {
        _netData.value = bundle.get("data") as ArrayList<NetworkActivityRecord>
    }

    class NetworkStatsViewModelFactory(
        private val application: Application,
        private val applicationInfo: ApplicationInfo
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) = AppViewModel(application, applicationInfo) as T
    }
}
