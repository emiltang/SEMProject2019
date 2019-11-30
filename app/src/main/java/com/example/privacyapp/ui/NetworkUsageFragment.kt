package com.example.privacyapp.ui

import android.app.usage.NetworkStats
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.privacyapp.R
import kotlinx.android.synthetic.main.fragment_network_usage.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class NetworkUsageFragment : Fragment(R.layout.fragment_network_usage), AppResultReceiver.AppReceiver,
    View.OnClickListener {

    private val viewModel: NetworkStatsViewModel by lazy {
        val factory = NetworkStatsViewModelFactory(this.activity!!.application, application)
        return@lazy ViewModelProviders.of(this, factory)[NetworkStatsViewModel::class.java]
    }

    private lateinit var application: ApplicationInfo
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        application = arguments!!.getParcelable("model") ?: return

        viewModel.data.observe(this, Observer { networkStatsView.append("$it") })

        appIcon.setImageDrawable(application.loadIcon(context!!.packageManager))
        appTitle.text = context!!.packageManager.getApplicationLabel(application)
        permissionButton.setOnClickListener(this)
        openPermSettingsButton.setOnClickListener(this)

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

    override fun onClick(v: View?) = when (v!!.id) {
        R.id.openPermSettingsButton -> {
            val myAppSettings = Intent(ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse(application.packageName))
            myAppSettings.addCategory(Intent.CATEGORY_DEFAULT)
            myAppSettings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivityForResult(myAppSettings, 0)
        }
        R.id.permissionButton -> navController.navigate(
            R.id.action_networkUsageFragment_to_permissionFragment,
            bundleOf("application" to application)
        )
        else -> {
        }
    }
}




