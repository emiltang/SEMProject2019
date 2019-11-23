package com.example.privacyapp.ui

import android.app.usage.NetworkStats
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.privacyapp.NetworkUsageService
import com.example.privacyapp.R
import kotlinx.android.synthetic.main.fragment_network_usage.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class NetworkUsageFragment : Fragment(R.layout.fragment_network_usage), AppResultReceiver.AppReceiver {


    private lateinit var application: ApplicationInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        application = arguments!!.getParcelable<ApplicationInfo>("model") as ApplicationInfo
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appIcon.setImageDrawable(application.loadIcon(context!!.packageManager))
        appTitle.text = context!!.packageManager.getApplicationLabel(application)

        // Start service to fetch netstats data and request callback
        val intent = Intent(context!!, NetworkUsageService::class.java)
        intent.putExtra("resultReceiver", AppResultReceiver(this))
        intent.putExtra("uid", application.uid)
        NetworkUsageService.enqueueWork(context!!, intent)
    }

    override fun onReceiveResult(resultCode: Int, bundle: Bundle) {
        val list = bundle.get("data")
        if (list !is ArrayList<*>) return // smart cast
        statsView.text = "" // clear textfield
        for (item in list) {
            if (item !is NetworkStats.Bucket) continue // smart cast
            /* Convert unix time to human readable format */
            val time = LocalDateTime.ofInstant(Instant.ofEpochMilli(item.endTimeStamp), ZoneId.systemDefault())
            statsView.append("$time ${item.rxPackets} ${item.txPackets}\n")
        }
    }
}




