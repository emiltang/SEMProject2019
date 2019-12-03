package com.example.privacyapp.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.privacyapp.R
import com.example.privacyapp.service.UploadWorker
import kotlinx.android.synthetic.main.fragment_create_warning.*

class CreateWarningFragment : Fragment(R.layout.fragment_create_warning), View.OnClickListener {

    lateinit var workManager: WorkManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sendButton.setOnClickListener(this)
        workManager = WorkManager.getInstance(context!!)
    }

    private fun handleSendButton(view: View) {

        val data = Data.Builder()
            .putString("app", appTitle.text.toString())
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
}
