package com.example.privacyapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.privacyapp.model.NetworkActivityRecord

@Database(entities = [NetworkActivityRecord::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun networkActivityRecordDao(): NetworkActivityRecordDao

    companion object {
        private var instance: AppDatabase? = null

        operator fun invoke(context: Context) = instance
            ?: buildDatabase(context).also { instance = it }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDatabase::class.java, "privacy-app.db").build()
    }
}