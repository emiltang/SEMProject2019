package com.example.privacyapp.ui


import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.privacyapp.R
import kotlinx.android.synthetic.main.fragment_network_usage.*

class NetworkUsageFragment : Fragment(R.layout.fragment_network_usage) {

    private lateinit var application: ApplicationInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        application = arguments!!.getParcelable<ApplicationInfo>("model") as ApplicationInfo
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appIcon.setImageDrawable(application.loadIcon(context!!.packageManager))
        appTitle.text = context!!.packageManager.getApplicationLabel(application)
    }
}

