package com.example.privacyapp.db

import androidx.room.TypeConverter
import java.util.*

class Converters {
    @TypeConverter
    fun fromString(string: String) = UUID.fromString(string)

    @TypeConverter
    fun uuidToString(uuid: UUID) = uuid.toString()
}
