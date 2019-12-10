package com.example.privacyapp.ui.fragment

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.privacyapp.R
import com.example.privacyapp.ui.NetworkViewAdapter
import com.example.privacyapp.ui.viewmodel.AppViewModel
import kotlinx.android.synthetic.main.fragment_app_detail.*

class AppDetailFragment : Fragment(R.layout.fragment_app_detail), View.OnClickListener {

    private val viewModel: AppViewModel by lazy {
        val factory = AppViewModel.NetworkStatsViewModelFactory(this.activity!!.application, application)
        return@lazy ViewModelProviders.of(this, factory)[AppViewModel::class.java]
    }

    @Suppress("RemoveExplicitTypeArguments")
    private val application: ApplicationInfo by lazy {
        arguments!!.getParcelable<ApplicationInfo>("model") as ApplicationInfo
    }

    private val navController by lazy { findNavController() }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.warnings.observe(this, Observer { list ->
            list.forEach { warningLabel.append("${it.description}\n") }
        })

        appIcon.setImageDrawable(application.loadIcon(context!!.packageManager))
        description.text = context!!.packageManager.getApplicationLabel(application)

        permissionButton.setOnClickListener(this)
        openPermSettingsButton.setOnClickListener(this)

        recyclerView.layoutManager = LinearLayoutManager(this.context)
        val adapter = NetworkViewAdapter(context!!).also { recyclerView.adapter = it }
        viewModel.netData.observe(this, Observer { adapter.list = it })
    }

    override fun onClick(v: View?) = when (v!!.id) {
        R.id.openPermSettingsButton -> openAppSettings()
        R.id.permissionButton -> navigateDetail()
        else -> Unit
    }

    private fun openAppSettings() = with(Intent()) {
        action = ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package", application.packageName, null)
        startActivity(this)
    }

    private fun navigateDetail() = navController.navigate(
        R.id.action_networkUsageFragment_to_permissionFragment,
        bundleOf("application" to application)
    )
}
