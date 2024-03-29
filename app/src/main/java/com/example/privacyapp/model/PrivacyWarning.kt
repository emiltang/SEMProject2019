package com.example.privacyapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "warning")
data class PrivacyWarning(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "app")
    val app: String = "",
    @ColumnInfo(name = "permission")
    val permission: String = "",
    @ColumnInfo(name = "description")
    val description: String = ""
)
