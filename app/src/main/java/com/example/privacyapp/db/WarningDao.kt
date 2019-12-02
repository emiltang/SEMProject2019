package com.example.privacyapp.db


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.privacyapp.model.Warning

@Dao
interface WarningDao {
    @Insert
    fun insertAll(vararg records: Warning)

    @Insert
    fun insertAll(records: List<Warning>)

    @Query("SELECT * FROM warning")
    fun getAll(): LiveData<List<Warning>>

    @Query("SELECT * FROM warning")
    fun findById(): List<Warning>
}