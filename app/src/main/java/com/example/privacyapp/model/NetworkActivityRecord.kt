package com.example.privacyapp.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "NetworkActivityRecord")
data class NetworkActivityRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "appUid") val appUid: Int,
    @ColumnInfo(name = "data") val data: Long
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(appUid)
        parcel.writeLong(data)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<NetworkActivityRecord> {
        override fun createFromParcel(parcel: Parcel) = NetworkActivityRecord(parcel)
        override fun newArray(size: Int): Array<NetworkActivityRecord?> = arrayOfNulls(size)
    }
}