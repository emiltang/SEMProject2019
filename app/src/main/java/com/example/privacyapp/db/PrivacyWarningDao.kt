package com.example.privacyapp.db

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.privacyapp.model.PrivacyWarning

@Dao
interface PrivacyWarningDao {

    @Insert
    suspend fun insertAll(vararg records: PrivacyWarning)

    @Insert
    suspend fun insertAll(records: List<PrivacyWarning>)

    @Query("SELECT * FROM warning")
    fun getAll(): LiveData<List<PrivacyWarning>>

    @Query("SELECT * FROM warning")
    fun findById(): List<PrivacyWarning>

    @Query("SELECT * FROM warning ")
    fun selectAll(): Cursor
}
