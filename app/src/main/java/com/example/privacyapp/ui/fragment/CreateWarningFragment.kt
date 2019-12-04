package com.example.privacyapp.ui.fragment

import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.privacyapp.R
import com.example.privacyapp.service.UploadWorker
import kotlinx.android.synthetic.main.fragment_create_warning.*

class CreateWarningFragment : Fragment(R.layout.fragment_create_warning), AdapterView.OnItemSelectedListener, View.OnClickListener {

    lateinit var workManager: WorkManager

    private val list = mutableListOf<String>()
    private var apps = emptyList<ApplicationInfo>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appTitleSpinner.onItemSelectedListener = this
        sendButton.setOnClickListener(this)
        workManager = WorkManager.getInstance(context!!)
        loadApplicationInfoToSpinner()
    }

    private fun handleSendButton(view: View) {

        val data = Data.Builder()
            .putString("app", appTitleSpinner.selectedItem.toString())
            .putString("permission", permission.text.toString())
            .putString("description", description.text.toString())
            .build()
        val request = OneTimeWorkRequest.Builder(UploadWorker::class.java).setInputData(data).build()
        workManager.enqueue(request)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.sendButton -> handleSendButton(view)
        }
    }

    private fun loadApplicationInfoToSpinner () {
        apps = context!!.packageManager.getInstalledApplications(0)
        apps.forEach{
            list.add(context!!.packageManager.getApplicationLabel(it).toString())
        }
        val adapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        appTitleSpinner.adapter = adapter
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        println("The selected item is: " + list[position] + " is it the same " + appTitleSpinner.selectedItem.toString())
    }
}
