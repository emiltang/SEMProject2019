package com.example.privacyapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "warning")
data class PrivacyWarning(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "app")
    val app: String = "",
    @ColumnInfo(name = "permission")
    val permission: String = "",
    @ColumnInfo(name = "description")
    val description: String = ""
)
