package com.example.privacyapp.service

import android.annotation.SuppressLint
import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager.TYPE_MOBILE
import android.net.ConnectivityManager.TYPE_WIFI
import android.os.RemoteException
import android.os.ResultReceiver
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.JobIntentService
import androidx.core.os.bundleOf
import com.example.privacyapp.model.NetworkActivityRecord
import java.util.*

class NetworkUsageService : JobIntentService() {

    private val tag = this::class.simpleName

    private val telephonyManager by lazy { getSystemService(TELEPHONY_SERVICE) as TelephonyManager }
    private val networkStatsManager by lazy { getSystemService(NETWORK_STATS_SERVICE) as NetworkStatsManager }

    override fun onHandleWork(intent: Intent) {
        val uid = intent.getIntExtra("uid", NO_UID)
        if (uid == NO_UID) return

        val wifi = getNetworkStatsForUidWifi(uid)
        val mobile = getNetworkStatsForUidMobile(uid)

        val resultReceiver = intent.getParcelableExtra("resultReceiver") as ResultReceiver
        resultReceiver.send(0, bundleOf("data" to wifi + mobile))
    }

    private fun unpackBuckets(networkStats: NetworkStats) = sequence {
        while (networkStats.hasNextBucket()) {
            val bucket = NetworkStats.Bucket()
            networkStats.getNextBucket(bucket)
            yield(
                NetworkActivityRecord(
                    startTime = bucket.startTimeStamp,
                    endTime = bucket.endTimeStamp,
                    downBytes = bucket.rxBytes.toInt(),
                    upBytes = bucket.txBytes.toInt()
                )
            )
        }
    }

    @Suppress("DEPRECATION")
    private fun getNetworkStatsForUidWifi(uid: Int) = try {
        networkStatsManager.queryDetailsForUid(
            TYPE_WIFI,
            "",
            0,
            tomorrow(),
            uid
        ).use(::unpackBuckets).asIterable()
    } catch (ex: RemoteException) {
        Log.e(tag, "Network error", ex)
        emptyList<NetworkActivityRecord>()
    }

    /**
     * Broken in android 10
     * https://stackoverflow.com/questions/55173823/i-am-getting-imei-null-in-android-q
     */
    @Suppress("DEPRECATION")
    @SuppressLint("HardwareIds")
    private fun getNetworkStatsForUidMobile(uid: Int) = try {
        networkStatsManager.queryDetailsForUid(
            TYPE_MOBILE,
            telephonyManager.subscriberId,
            0,
            tomorrow(),
            uid
        ).use(::unpackBuckets).asIterable()
    } catch (ex: RemoteException) {
        Log.e(tag, "Network error", ex)
        emptyList<NetworkActivityRecord>()
    } catch (ex: SecurityException) {
        Log.e(tag, "Missing permission", ex)
        emptyList<NetworkActivityRecord>()
    }


    companion object {
        private const val NO_UID = -10
        private const val JOB_ID = 100

        fun enqueueWork(context: Context, intent: Intent) = enqueueWork(
            context, NetworkUsageService::class.java, JOB_ID, intent
        )

        private fun tomorrow() = Calendar.getInstance().also { it.add(Calendar.DATE, 1) }.timeInMillis
    }
}
