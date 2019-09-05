package com.example.httpdatawidget.storage

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.example.httpdatawidget.storage.DatasourceInfo

@Dao
interface DatasourceInfoDao {

    @Query("SELECT * from datasourceInfo")
    fun getAll(): LiveData<List<DatasourceInfo>>

    @Query("SELECT * FROM datasourceInfo WHERE id = :id")
    fun findById(id: Long): DatasourceInfo

    @Insert(onConflict = REPLACE)
    fun insert(datasourceInfo: DatasourceInfo)

    @Update
    fun update(datasourceInfo: DatasourceInfo)

    @Query("DELETE from datasourceInfo")
    fun deleteAll()
}