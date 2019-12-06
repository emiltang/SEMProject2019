package com.example.privacyapp.model

import android.os.Parcel
import android.os.Parcelable

data class NetworkActivityRecord(
    val endTime: Long,
    val startTime: Long,
    val downBytes: Int,
    val upBytes: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readLong(),
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(endTime)
        parcel.writeLong(startTime)
        parcel.writeInt(downBytes)
        parcel.writeInt(upBytes)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NetworkActivityRecord> {
        override fun createFromParcel(parcel: Parcel): NetworkActivityRecord {
            return NetworkActivityRecord(parcel)
        }

        override fun newArray(size: Int): Array<NetworkActivityRecord?> {
            return arrayOfNulls(size)
        }
    }
}
