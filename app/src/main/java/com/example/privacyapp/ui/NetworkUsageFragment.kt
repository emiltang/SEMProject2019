package com.example.privacyapp.ui

import android.app.usage.NetworkStats
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.privacyapp.NetworkUsageService
import com.example.privacyapp.R
import kotlinx.android.synthetic.main.fragment_network_usage.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class NetworkUsageFragment : Fragment(R.layout.fragment_network_usage), AppResultReceiver.AppReceiver,
    View.OnClickListener {

    private lateinit var application: ApplicationInfo
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        application = arguments!!.getParcelable("model") ?: return

        appIcon.setImageDrawable(application.loadIcon(context!!.packageManager))
        appTitle.text = context!!.packageManager.getApplicationLabel(application)
        permissionButton.setOnClickListener(this)

        // Start service to fetch netstats data and request callback
        val intent = Intent(context!!, NetworkUsageService::class.java).apply {
            putExtra("resultReceiver", AppResultReceiver(listener = this@NetworkUsageFragment))
            putExtra("uid", application.uid)
        }
        NetworkUsageService.enqueueWork(context!!, intent)


    }

    override fun onReceiveResult(resultCode: Int, bundle: Bundle) {
        val list = bundle.get("data")
        if (list !is ArrayList<*>) return // smart cast
        networkStatsView.text = "" // clear textfield
        for (item in list) {
            if (item !is NetworkStats.Bucket) continue // smart cast
            /* Convert unix time to human readable format */
            val time = LocalDateTime.ofInstant(Instant.ofEpochMilli(item.endTimeStamp), ZoneId.systemDefault())
            networkStatsView.append("$time ${item.rxPackets} ${item.txPackets}\n")
        }
    }

    override fun onClick(v: View?) {
        if (v!!.id == R.id.permissionButton) navController.navigate(
            R.id.action_networkUsageFragment_to_permissionFragment,
            bundleOf("application" to application)
        )
    }
}




