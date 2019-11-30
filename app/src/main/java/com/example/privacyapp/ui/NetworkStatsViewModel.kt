package com.example.privacyapp.ui

import android.app.Application
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.privacyapp.NetworkUsageService
import com.example.privacyapp.model.NetworkActivityRecord

class NetworkStatsViewModel(
    application: Application,
    val applicationInfo: ApplicationInfo
) : AndroidViewModel(application), AppResultReceiver.AppReceiver {

    val data: MutableLiveData<List<NetworkActivityRecord>> get() = _data

    private val _data: MutableLiveData<List<NetworkActivityRecord>> by lazy {
        MutableLiveData<List<NetworkActivityRecord>>().also {
            load()
        }
    }

    private fun load() {
        val intent = Intent(getApplication(), NetworkUsageService::class.java).apply {
            putExtra("resultReceiver", AppResultReceiver(listener = this@NetworkStatsViewModel))
            putExtra("uid", applicationInfo.uid)
        }
        NetworkUsageService.enqueueWork(getApplication(), intent)
    }

    override fun onReceiveResult(resultCode: Int, bundle: Bundle) {
        _data.value = bundle.get("data") as ArrayList<NetworkActivityRecord>
    }


}

class NetworkStatsViewModelFactory(
    val application: Application,
    val applicationInfo: ApplicationInfo
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = NetworkStatsViewModel(application, applicationInfo) as T
}