package com.example.privacyapp.ui.fragment

import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.privacyapp.R
import com.example.privacyapp.ui.AppListAdapter
import kotlinx.android.synthetic.main.activity_list_activity.*

/**
 * Display list of installed apps
 */
class AppListFragment : Fragment(R.layout.fragment_app_list),
    AppListAdapter.AppListItemClickListener {

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        val apps = context!!.packageManager.getInstalledApplications(0)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter =
            AppListAdapter(context!!, apps, this)
    }

    /**
     * Activated on list item clicked
     */
    override fun onItemClicked(model: ApplicationInfo) = navController.navigate(
        R.id.action_appListFragment_to_networkUsageFragment,
        bundleOf("model" to model)
    )
}
