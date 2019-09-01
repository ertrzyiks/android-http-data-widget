package com.example.httpdatawidget

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "datasourceInfo")
data class DatasourceInfo(
    @PrimaryKey(autoGenerate = true) var id: Long?,
    @ColumnInfo(name = "name") var name: String,

    @ColumnInfo(name = "url") var url: String) {

    constructor():this(null,"", "")
}