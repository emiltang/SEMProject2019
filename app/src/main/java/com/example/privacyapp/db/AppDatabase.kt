package com.example.privacyapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.privacyapp.model.PrivacyWarning

/**
 * Database abstraction
 */
@Database(entities = [PrivacyWarning::class], version = 1)

@TypeConverters(Converters::class)

abstract class AppDatabase : RoomDatabase() {

    abstract fun privacyWarningDao(): PrivacyWarningDao

    companion object {

        private var instance: AppDatabase? = null

        /**
         * Database singleton getter
         */
        operator fun invoke(context: Context) = instance ?: buildDatabase(context).also { instance = it }

        /**
         * Build in memory database
         */
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        }
    }
}
