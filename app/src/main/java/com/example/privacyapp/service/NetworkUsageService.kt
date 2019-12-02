package com.example.privacyapp.service

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
import com.example.privacyapp.model.NetworkActivityRecord
import java.util.*
import kotlin.collections.ArrayList


@Suppress("DEPRECATION")
class NetworkUsageService : JobIntentService() {

    private lateinit var networkStatsManager: NetworkStatsManager
    private val tag = this::class.simpleName

    override fun onCreate() {
        super.onCreate()
        networkStatsManager = applicationContext.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
    }

    override fun onHandleWork(intent: Intent) {
        val uid = intent.getIntExtra(
            "uid",
            NO_UID
        )
        if (uid == NO_UID) return

        val wifi = getNetworkStatsForUidWifi(uid)
        val mobile = getNetworkStatsForUidMobile(uid)

        val resultReceiver = intent.getParcelableExtra("resultReceiver") as ResultReceiver
        resultReceiver.send(0, bundleOf("data" to wifi + mobile))
    }

    private fun unpackBuckets(networkStats: NetworkStats): List<NetworkActivityRecord> {
        val records = ArrayList<NetworkStats.Bucket>()
        while (networkStats.hasNextBucket()) {
            val bucket = NetworkStats.Bucket()
            networkStats.getNextBucket(bucket)
            records.add(bucket)
        }
        return records.map {
            NetworkActivityRecord(
                endTime = it.endTimeStamp,
                downBytes = it.rxBytes.toInt(),
                upBytes = it.txBytes.toInt()
            )
        }
    }

    private fun getNetworkStatsForUidWifi(uid: Int): List<NetworkActivityRecord> {
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

    private fun getNetworkStatsForUidMobile(uid: Int): List<NetworkActivityRecord> {
        /*
         * Broken in android 10
         * https://stackoverflow.com/questions/55173823/i-am-getting-imei-null-in-android-q
         */
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
        } catch (ex: SecurityException) {
            Log.e(tag, "Missing permission", ex)
            return emptyList()
        }
        return unpackBuckets(networkStats = networkStats)
    }

    companion object {
        private const val NO_UID = -10
        private const val JOB_ID = 100

        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(
                context, NetworkUsageService::class.java,
                JOB_ID, intent
            )
        }
    }
}
