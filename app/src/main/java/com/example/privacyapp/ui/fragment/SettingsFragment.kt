package com.example.privacyapp.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceChangeListener
import androidx.preference.Preference.OnPreferenceClickListener
import androidx.preference.PreferenceFragmentCompat
import androidx.work.*
import com.example.privacyapp.R
import com.example.privacyapp.service.SyncWorker
import java.util.*
import java.util.concurrent.TimeUnit

class SettingsFragment : PreferenceFragmentCompat() {

    private val workManager by lazy { WorkManager.getInstance(context!!) }
    private var workId: UUID? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findPreference<Preference>("continuous_sync")?.apply {
            onPreferenceChangeListener = OnPreferenceChangeListener(::toggleSync)
        }

        findPreference<Preference>("sync_now")?.apply {
            onPreferenceClickListener = OnPreferenceClickListener(::syncNow)
        }
    }

    private fun toggleSync(pref: Preference, obj: Any): Boolean {

        val newValue = obj as Boolean

        if (newValue) {
            val constraints = Constraints.Builder()
                .setRequiresCharging(true)
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .build()
            val request = PeriodicWorkRequest.Builder(SyncWorker::class.java, 1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .build()
            workId = request.id
            workManager.enqueue(request)
        } else workId?.let { workManager.cancelWorkById(it) }
        return true
    }

    private fun syncNow(pref: Preference): Boolean {
        val request = OneTimeWorkRequest.from(SyncWorker::class.java)
        workManager.enqueue(request)
        return true
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}