package com.example.privacyapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.privacyapp.db.AppDatabase

class WarningViewModel(application: Application) : AndroidViewModel(application), IWarningViewModel {

    val db by lazy { AppDatabase(application) }
    override val warnings = db.privacyWarningDao().getAll()

    class WarningViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>) = WarningViewModel(application) as T
    }

}