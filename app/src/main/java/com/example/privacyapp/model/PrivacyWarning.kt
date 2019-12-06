package com.example.privacyapp.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "warning")
data class PrivacyWarning(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "app") val app: String,
    @ColumnInfo(name = "permission") val permission: String,
    @ColumnInfo(name = "description") val description: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(app)
        parcel.writeString(permission)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PrivacyWarning> {
        override fun createFromParcel(parcel: Parcel): PrivacyWarning {
            return PrivacyWarning(parcel)
        }

        override fun newArray(size: Int): Array<PrivacyWarning?> {
            return arrayOfNulls(size)
        }
    }
}