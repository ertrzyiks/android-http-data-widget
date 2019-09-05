package com.example.httpdatawidget.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(DatasourceInfo::class), version = 1)
abstract class DatasourceInfoBase : RoomDatabase() {
    abstract fun datasourceInfoDao(): DatasourceInfoDao

    companion object {

        private var INSTANCE: DatasourceInfoBase? = null

        fun getInstance(context: Context): DatasourceInfoBase? {
            if (INSTANCE == null) {
                synchronized(DatasourceInfoBase::class) {
                    INSTANCE = Room.inMemoryDatabaseBuilder(context.getApplicationContext(),
                        DatasourceInfoBase::class.java)
                        .build()
                }
            }
            return INSTANCE
        }
    }
}