package com.example.httpdatawidget.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(DatasourceInfo::class, WidgetConfig::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun datasourceInfoDao(): DatasourceInfoDao
    abstract fun widgetConfigDao(): WidgetConfigDao

    companion object {

        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase::class.java,
                        "remote_data_widget")
                        .build()

                }
            }
            return INSTANCE!!
        }
    }
}