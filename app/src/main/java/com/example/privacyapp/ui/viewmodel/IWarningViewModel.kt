package com.example.privacyapp.ui.viewmodel

import androidx.lifecycle.LiveData
import com.example.privacyapp.model.PrivacyWarning

interface IWarningViewModel {
    val warnings: LiveData<List<PrivacyWarning>>
}