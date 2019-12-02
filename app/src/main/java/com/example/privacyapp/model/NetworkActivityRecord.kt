package com.example.privacyapp.model

import android.os.Parcel
import android.os.Parcelable

data class NetworkActivityRecord(
    val endTime: Long,
    val downBytes: Int,
    val upBytes: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        endTime = parcel.readLong(),
        downBytes = parcel.readInt(),
        upBytes = parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(endTime)
        parcel.writeInt(downBytes)
        parcel.writeInt(upBytes)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<NetworkActivityRecord> {
        override fun createFromParcel(parcel: Parcel) = NetworkActivityRecord(parcel)

        override fun newArray(size: Int): Array<NetworkActivityRecord?> = arrayOfNulls(size)
    }
}