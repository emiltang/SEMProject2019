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
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.privacyapp.R
import com.example.privacyapp.ui.viewmodel.AppViewModel
import kotlinx.android.synthetic.main.fragment_app_detail.*

class AppDetailFragment : Fragment(R.layout.fragment_app_detail), View.OnClickListener {


    private val viewModel: AppViewModel by lazy {
        val factory = AppViewModel.NetworkStatsViewModelFactory(this.activity!!.application, application)
        return@lazy ViewModelProviders.of(this, factory)[AppViewModel::class.java]
    }

    private lateinit var application: ApplicationInfo
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        application = arguments!!.getParcelable("model") ?: return

        viewModel.warnings.observe(
            this,
            Observer { list ->
                list.filter { it.app == application.packageName.toString() }
                    .forEach { warningLabel.text = it.description }
            })
        //viewModel.netData.observe(this, Observer { networkStatsView.append("$it") })

        appIcon.setImageDrawable(application.loadIcon(context!!.packageManager))
        appTitle.text = context!!.packageManager.getApplicationLabel(application)
        permissionButton.setOnClickListener(this)
        openPermSettingsButton.setOnClickListener(this)


        recyclerView.layoutManager = LinearLayoutManager(this.context)
//        val adapter= NetworkViewAdapter()
//        recyclerView.adapter = adapter
        viewModel.netData.observe(this, Observer { recyclerView.adapter = NetworkViewAdapter(it) })

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




