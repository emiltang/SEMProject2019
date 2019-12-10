package com.example.privacyapp.ui.fragment

import android.content.pm.PackageManager.GET_PERMISSIONS
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.work.*
import com.example.privacyapp.R
import com.example.privacyapp.db.AppDatabase
import com.example.privacyapp.model.PrivacyWarning
import com.example.privacyapp.service.SyncWorker
import com.example.privacyapp.service.UploadWorker
import kotlinx.android.synthetic.main.fragment_create_warning.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateWarningFragment : Fragment(R.layout.fragment_create_warning), AdapterView.OnItemSelectedListener,
    View.OnClickListener {

    private val workManager by lazy { WorkManager.getInstance(context!!) }
    private val apps by lazy { context!!.packageManager.getInstalledApplications(0) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appTitleSpinner.onItemSelectedListener = this
        appPermissionSpinner.onItemSelectedListener = this
        sendButton.setOnClickListener(this)
        syncButton.setOnClickListener(this)
        loadApplicationInfoToSpinner()
    }

    private fun startUploadWorker() = CoroutineScope(Dispatchers.IO).launch {
        val warning = PrivacyWarning(
            app = appTitleSpinner.selectedItem.toString(),
            permission = appPermissionSpinner.selectedItem.toString(),
            description = description.text.toString()
        )
        AppDatabase(context!!)
            .privacyWarningDao()
            .insertAll(warning)
        val data = Data.Builder()
            .putString("id", warning.id.toString())
            .build()
        /* Schedule upload on wifi and power */
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresCharging(true)
            .build()
        val request = OneTimeWorkRequest.Builder(UploadWorker::class.java)
            .setInputData(data)
            .setConstraints(constraints)
            .build()
        workManager.enqueue(request)
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.sendButton -> startUploadWorker()
            R.id.syncButton -> startSyncWorker()
        }
    }

    private fun startSyncWorker() {
        val request = OneTimeWorkRequest.from(SyncWorker::class.java)
        workManager.enqueue(request)
    }

    private fun loadApplicationInfoToSpinner() {
        val appNamesList = apps.map { it.packageName }
        val adapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, appNamesList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        appTitleSpinner.adapter = adapter
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) = when (parent?.id) {
        R.id.appTitleSpinner -> setAppPermissionSpinner(position)
        R.id.appPermissionSpinner -> Unit
        else -> Unit
    }

    private fun setAppPermissionSpinner(position: Int) {
        val appInfo = context!!.packageManager.getPackageInfo(apps[position].packageName, GET_PERMISSIONS)
        val permissions = appInfo.requestedPermissions ?: emptyArray()
        val adapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, permissions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        appPermissionSpinner.adapter = adapter
    }
}
