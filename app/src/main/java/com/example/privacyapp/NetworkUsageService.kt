package com.example.privacyapp

import android.annotation.SuppressLint
import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.RemoteException
import android.os.ResultReceiver
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.JobIntentService
import androidx.core.os.bundleOf
import com.example.privacyapp.db.AppDatabase
import java.util.*
import kotlin.collections.ArrayList


@Suppress("DEPRECATION")
class NetworkUsageService : JobIntentService() {

    private lateinit var db: AppDatabase
    private lateinit var networkStatsManager: NetworkStatsManager
    private val tag = this::class.simpleName

    override fun onCreate() {
        super.onCreate()
        networkStatsManager = applicationContext.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
        db = AppDatabase(applicationContext)
    }

    override fun onHandleWork(intent: Intent) {

        val uid = intent.getIntExtra("uid", -1)

        if (uid == -1) return

        val resultReceiver = intent.getParcelableExtra("resultReceiver") as ResultReceiver

        val wifi = getNetworkStatsForUidWifi(uid)
        val mobile = getNetworkStatsForUidMobile(uid)

        //db.networkActivityRecordDao().insertAll(wifi)
        //db.networkActivityRecordDao().insertAll(mobile)

        resultReceiver.send(0, bundleOf("data" to wifi + mobile))
    }

    private fun unpackBuckets(networkStats: NetworkStats): List<NetworkStats.Bucket> {
        val records = ArrayList<NetworkStats.Bucket>()
        while (networkStats.hasNextBucket()) {
            val bucket = NetworkStats.Bucket()
            networkStats.getNextBucket(bucket)
            records.add(bucket)
        }
        return records
    }

    private fun getNetworkStatsForUidWifi(uid: Int): List<NetworkStats.Bucket> {
        val networkStats: NetworkStats?
        val tomorrow = Calendar.getInstance().also { it.add(Calendar.DATE, 1) }
        try {
            networkStats = networkStatsManager.queryDetailsForUid(
                ConnectivityManager.TYPE_WIFI,
                "",
                0,
                tomorrow.timeInMillis,
                uid
            )
        } catch (ex: RemoteException) {
            Log.e(tag, "Network error", ex)
            return emptyList()
        }
        return unpackBuckets(networkStats = networkStats)
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    private fun getNetworkStatsForUidMobile(uid: Int): List<NetworkStats.Bucket> {
        val networkStats: NetworkStats?
        val tomorrow = Calendar.getInstance().also { it.add(Calendar.DATE, 1) }

        try {
            val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            networkStats = networkStatsManager.queryDetailsForUid(
                ConnectivityManager.TYPE_MOBILE,
                telephonyManager.subscriberId,
                0,
                tomorrow.timeInMillis,
                uid
            )
        } catch (ex: RemoteException) {
            Log.e(tag, "Network error", ex)
            return emptyList()
        }
        return unpackBuckets(networkStats = networkStats)
    }

    private fun getNetworkStatsForWifi(): List<NetworkStats.Bucket> {
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
            return emptyList()
        }
        return unpackBuckets(networkStats = networkStats)
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    private fun getNetworkStatsForMobile(): List<NetworkStats.Bucket> {
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
            return emptyList()
        }
        return unpackBuckets(networkStats = networkStats)
    }

    companion object {
        private const val JOB_ID = 100

        fun enqueueWork(context: Context, intent: Intent) =
            enqueueWork(context, NetworkUsageService::class.java, JOB_ID, intent)
    }
}
