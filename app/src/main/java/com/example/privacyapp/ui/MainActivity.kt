package com.example.privacyapp.ui

import android.Manifest.permission.READ_PHONE_STATE
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.privacyapp.R
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var navController: NavController
    private val tag = this::class.simpleName

    @Suppress("DEPRECATION")
    private fun askPermission() {
        /* Check if PACKAGE_USAGE_STATS permission is given*/
        val appOps: AppOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), packageName)
        if (mode == AppOpsManager.MODE_ALLOWED) {
            return
        }
        /* Ask permissions */
        startActivity(Intent(ACTION_USAGE_ACCESS_SETTINGS))
        requestPermissions(arrayOf(READ_PHONE_STATE), 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        askPermission()
        /* Setup toolbar and drawer */
        navController = findNavController(R.id.nav_host_fragment)

        toolbar.setupWithNavController(navController, drawer_layout)
        nav_view.setupWithNavController(navController)
        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.warning -> navController.navigate(R.id.createWarningFragment)
        }
        return true
    }
}
