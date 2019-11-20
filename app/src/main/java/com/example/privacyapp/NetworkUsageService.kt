package com.example.privacyapp

import android.annotation.SuppressLint
import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.RemoteException
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.JobIntentService


@Suppress("DEPRECATION")
class NetworkUsageService : JobIntentService() {

    private lateinit var networkStatsManager: NetworkStatsManager
    private val tag = this::class.simpleName

    override fun onHandleWork(intent: Intent) {
        networkStatsManager = applicationContext.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager

        val wifi = getNetworkStatsForWifi()
        val mobile = getNetworkStatsForMobile()

        wifi.addAll(mobile)

        val activityIntent = Intent(baseContext, ListActivity::class.java)
        activityIntent.putParcelableArrayListExtra("data", wifi)
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // WTF Some how need only for android 6 >= and 9>=
        startActivity(activityIntent)
    }

    private fun getNetworkStatsForWifi(): ArrayList<NetworkActivityRecord> {
        val networkStats: NetworkStats?
        try {
            networkStats = networkStatsManager.queryDetails(
                ConnectivityManager.TYPE_WIFI,
                "",
                0,
                System.currentTimeMillis()
            )
        } catch (ex: RemoteException) {
            Log.e(tag, "Network error", ex)
            return arrayListOf()
        }
        return unpackBuckets(networkStats = networkStats)
    }

    private fun unpackBuckets(networkStats: NetworkStats): ArrayList<NetworkActivityRecord> {
        val records = ArrayList<NetworkActivityRecord>()
        val bucket = NetworkStats.Bucket()
        while (networkStats.hasNextBucket()) {
            networkStats.getNextBucket(bucket)
            records.add(
                NetworkActivityRecord(
                    uid = bucket.uid,
                    data = bucket.rxBytes + bucket.txBytes
                )
            )
        }
        return records
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    private fun getNetworkStatsForMobile(): ArrayList<NetworkActivityRecord> {
        val networkStats: NetworkStats?
        try {
            val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            networkStats = networkStatsManager.queryDetails(
                ConnectivityManager.TYPE_MOBILE,
                telephonyManager.subscriberId,
                0,
                System.currentTimeMillis()
            )
        } catch (ex: RemoteException) {
            Log.e(tag, "Network error", ex)
            return arrayListOf()
        }
        return unpackBuckets(networkStats = networkStats)
    }

    companion object {
        private const val JOB_ID = 100

        fun enqueueWork(context: Context, intent: Intent) =
            enqueueWork(context, NetworkUsageService::class.java, JOB_ID, intent)
    }
}
