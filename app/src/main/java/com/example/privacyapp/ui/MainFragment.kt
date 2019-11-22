package com.example.privacyapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.privacyapp.NetworkUsageService
import com.example.privacyapp.R


class MainFragment : Fragment(R.layout.fragment_main), View.OnClickListener {

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }


    @Suppress("UNUSED_PARAMETER")
    fun button(view: View) {
        val ids = context!!.packageManager.getInstalledApplications(0).map { it.uid }
        val intent = Intent(context, NetworkUsageService::class.java)
        intent.putExtra("uids", ids.toIntArray())
        NetworkUsageService.enqueueWork(context!!, intent)
    }

    companion object {

        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.button -> {
                button(v)
            }
        }
    }
}
