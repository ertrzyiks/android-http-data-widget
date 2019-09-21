package com.example.httpdatawidget.storage

import androidx.room.*

@Entity(tableName = "widgetConfigs",
    foreignKeys = arrayOf(
        ForeignKey(entity = DatasourceInfo::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("datasource_info_id"),
        onDelete = ForeignKey.SET_NULL)
    ),
    indices = arrayOf(Index(value = ["widgetId"], unique = true))
)
data class WidgetConfig(
    @PrimaryKey(autoGenerate = true) var id: Long?,
    @ColumnInfo(name = "label") var label: String,
    @ColumnInfo(name = "widgetId") var widgetId: Int,

    @ColumnInfo(name = "datasource_info_id") var datasource_info_id: Long?,
    @ColumnInfo(name = "path") var path: String,
    @ColumnInfo(name = "theme") var theme: String,
    @ColumnInfo(name = "update_interval_in_minutes") var update_interval_in_minutes: Int) {

    constructor(widgetId: Int):this(null,
        "", widgetId, null, "", "default", 0)
}