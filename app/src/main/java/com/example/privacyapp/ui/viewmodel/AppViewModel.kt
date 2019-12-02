package com.example.privacyapp.ui.viewmodel

import android.app.Application
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Bundle
import androidx.lifecycle.*
import com.example.privacyapp.db.AppDatabase
import com.example.privacyapp.model.NetworkActivityRecord
import com.example.privacyapp.model.Warning
import com.example.privacyapp.service.NetworkUsageService
import com.example.privacyapp.ui.AppResultReceiver

class AppViewModel(
    application: Application,
    private val applicationInfo: ApplicationInfo
) : AndroidViewModel(application), AppResultReceiver.AppReceiver {

    val netData: MutableLiveData<List<NetworkActivityRecord>> get() = _data

    val warnings: LiveData<List<Warning>> by lazy {
        AppDatabase(this.getApplication()).warningDao().getAll()
    }

    private val _data: MutableLiveData<List<NetworkActivityRecord>> by lazy {
        MutableLiveData<List<NetworkActivityRecord>>().also {
            load()
        }
    }

    private fun load() {
        val intent = Intent(getApplication(), NetworkUsageService::class.java).apply {
            putExtra(
                "resultReceiver",
                AppResultReceiver(listener = this@AppViewModel)
            )
            putExtra("uid", applicationInfo.uid)
        }
        NetworkUsageService.enqueueWork(getApplication(), intent)
    }

    override fun onReceiveResult(resultCode: Int, bundle: Bundle) {
        _data.value = bundle.get("data") as ArrayList<NetworkActivityRecord>
    }

    class NetworkStatsViewModelFactory(
        private val application: Application,
        private val applicationInfo: ApplicationInfo
    ) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) = AppViewModel(
            application,
            applicationInfo
        ) as T
    }
}

