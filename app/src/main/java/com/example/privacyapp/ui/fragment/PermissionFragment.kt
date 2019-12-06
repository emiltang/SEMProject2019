package com.example.privacyapp.ui.fragment

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.privacyapp.R
import com.example.privacyapp.ui.PermissionViewAdapter
import kotlinx.android.synthetic.main.fragment_permission.*


class PermissionFragment : Fragment(R.layout.fragment_permission),
    PermissionViewAdapter.OnListFragmentInteractionListener {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = arguments!!.getParcelable<ApplicationInfo>("application")
        val perm = context!!.packageManager.getPackageInfo(application!!.packageName, PackageManager.GET_PERMISSIONS)

        list.layoutManager = LinearLayoutManager(context)
        list.adapter = PermissionViewAdapter(
            context = context!!,
            application = application,
            permissions = perm.requestedPermissions ?: emptyArray(),
            listener = this
        )
    }


    override fun onItemClicked(item: String) {
        Toast.makeText(context!!, item, Toast.LENGTH_SHORT).show()
    }
}
