package com.example.privacyapp

import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.privacyapp.WarningListFragment.Arguments.ALL_APPS
import com.example.privacyapp.WarningListFragment.Arguments.SPECIFIC_APP
import com.example.privacyapp.ui.viewmodel.AppViewModel
import com.example.privacyapp.ui.viewmodel.IWarningViewModel
import com.example.privacyapp.ui.viewmodel.WarningViewModel
import kotlinx.android.synthetic.main.fragment_app_list.*


class WarningListFragment : Fragment(R.layout.fragment_warninglist_list) {

    val viewType by lazy {
        arguments!!.getSerializable(ARG_VIEW_TYPE) as Arguments
    }
    val applicationInfo by lazy { arguments!!.getParcelable<ApplicationInfo>(ARG_APPLICATION_INFO) as ApplicationInfo }


    @Suppress("USELESS_CAST")
    val viewModel: IWarningViewModel by lazy {
        when (viewType) {
            ALL_APPS -> {
                val factory = WarningViewModel.WarningViewModelFactory(activity!!.application)
                ViewModelProviders.of(this, factory)[WarningViewModel::class.java] as IWarningViewModel
            }
            SPECIFIC_APP -> {
                val factory = AppViewModel.NetworkStatsViewModelFactory(activity!!.application, applicationInfo)
                ViewModelProviders.of(this, factory)[AppViewModel::class.java] as IWarningViewModel
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = MyWarningListRecyclerViewAdapter()
        viewModel.warnings.observe(this, Observer { adapter.list = it })
        recyclerView.adapter = adapter

    }

    enum class Arguments { ALL_APPS, SPECIFIC_APP }


    companion object {


        const val ARG_APPLICATION_INFO = "application"

        const val ARG_VIEW_TYPE = "column-count"

        // TODO: Customize parameter initialization

    }
}
