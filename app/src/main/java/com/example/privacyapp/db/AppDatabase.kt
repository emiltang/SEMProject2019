package com.example.privacyapp.db

//
//import android.content.Context
//import androidx.room.Database
//import androidx.room.Room
//import androidx.room.RoomDatabase
//import com.example.privacyapp.model.NetworkActivityRecord
//
///**
// * Database abstraction
// */
//@Database(entities = [NetworkActivityRecord::class], version = 1)
//abstract class AppDatabase : RoomDatabase() {
//
//    abstract fun networkActivityRecordDao(): NetworkActivityRecordDao
//
//    companion object {
//
//        private var instance: AppDatabase? = null
//
//        /**
//         * Database singleton getter
//         */
//        operator fun invoke(context: Context) = instance ?: buildDatabase(context).also { instance = it }
//
//        /**
//         * Build in memory database
//         */
//        private fun buildDatabase(context: Context): AppDatabase {
//            return Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
//        }
//    }
//}
