package com.example.httpdatawidget.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update

@Dao
interface WidgetConfigDao {
    @Query("SELECT * FROM widgetConfigs WHERE widgetId = :id")
    fun findByWidgetId(id: Int): WidgetConfig?

    @Insert(onConflict = REPLACE)
    fun insert(datasourceInfo: WidgetConfig)

    @Update
    fun update(datasourceInfo: WidgetConfig)

    @Query("DELETE from widgetConfigs")
    fun deleteAll()

    @Query("DELETE FROM widgetConfigs WHERE id in (:ids)")
    fun deleteByIds(ids: IntArray)
}