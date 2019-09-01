package com.example.httpdatawidget

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface DatasourceInfoDao {

    @Query("SELECT * from datasourceInfo")
    fun getAll(): LiveData<List<DatasourceInfo>>

    @Insert(onConflict = REPLACE)
    fun insert(datasourceInfo: DatasourceInfo)

    @Query("DELETE from datasourceInfo")
    fun deleteAll()
}