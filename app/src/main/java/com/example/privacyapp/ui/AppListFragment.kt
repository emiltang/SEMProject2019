package com.example.privacyapp.ui


import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.privacyapp.R
import kotlinx.android.synthetic.main.activity_list_activity.*


class AppListFragment : Fragment(R.layout.fragment_app_list), RecyclerAdapter.MyOnItemClickListener {

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
    }

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        val apps = context!!.packageManager.getInstalledApplications(0)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = RecyclerAdapter(context!!, apps, this)
    }

    override fun onItemClicked(model: ApplicationInfo) {

        navController.navigate(R.id.action_appListFragment_to_networkUsageFragment, bundleOf("model" to model))
    }
}
