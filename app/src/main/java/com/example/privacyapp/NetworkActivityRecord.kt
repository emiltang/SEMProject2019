package com.example.privacyapp

import android.os.Parcel
import android.os.Parcelable

data class NetworkActivityRecord(val uid: Int, val data: Long) : Parcelable {

    constructor(parcel: Parcel) : this(
        uid = parcel.readInt(),
        data = parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(uid)
        parcel.writeLong(data)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<NetworkActivityRecord> {

        override fun createFromParcel(parcel: Parcel) = NetworkActivityRecord(parcel)

        override fun newArray(size: Int): Array<NetworkActivityRecord?> = arrayOfNulls(size)
    }
}