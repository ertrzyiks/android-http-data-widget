package com.example.httpdatawidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.RemoteViews
import com.example.httpdatawidget.storage.AppDatabase
import com.example.httpdatawidget.storage.DatasourceInfo
import com.example.httpdatawidget.storage.WidgetConfig
import java.lang.Exception
import android.app.AlarmManager
import android.os.PowerManager
import java.util.*

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [RemoteDataConfigureActivity]
 */
class RemoteData : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        println("ACTION: UPDATE")

        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            println("DATA: " + appWidgetId)
            updateAppWidget(context, appWidgetManager, appWidgetId)
//            Toast.makeText(context, "Widget has been updated! ", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            RemoteDataConfigureActivity.deleteTitlePref(context, appWidgetId)
        }

        Thread {
            AppDatabase.getInstance(context!!).widgetConfigDao().deleteByIds(appWidgetIds)
        }.start()
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
    }
    companion object {
        val AUTO_UPDATE = "com.example.httpdatawidget.AUTO_UPDATE"

        internal fun loadWidgetConfig(context: Context, widgetId: Int, callback: (config: WidgetConfig?, info: DatasourceInfo?) -> Unit) {
            val handler = Handler(Looper.getMainLooper())

            Thread {
                val db = AppDatabase.getInstance(context)
                var datasourceInfo: DatasourceInfo? = null
                val widgetConfig = db.widgetConfigDao().findByWidgetId(widgetId)

                if (widgetConfig !== null && widgetConfig.datasource_info_id !== null) {
                    datasourceInfo = db.datasourceInfoDao().findById(widgetConfig.datasource_info_id!!)
                }

                handler.post {
                    callback(widgetConfig, datasourceInfo)
                }
            }.start()
        }

        internal fun loadAndUpdateData(
            context: Context,
            views: RemoteViews,
            widgetId: Int,
            datasourceInfo: DatasourceInfo,
            path: String,
            callback: () -> Unit
        ) {
            views.setViewVisibility(R.id.appwidget_progressbar, View.VISIBLE)
            AppWidgetManager.getInstance(context).updateAppWidget(widgetId, views)

            LoadData(context, object: LoadDataCallback<Response>{
                override fun onSuccess(value: Response) {
                    var text = JsonSource.get(value.contentBody, path)
                    views.setTextViewText(R.id.appwidget_content, text)
                }

                override fun onFailure(e: Exception) {
                    views.setTextViewText(R.id.appwidget_label, "Error")
                    views.setTextViewText(R.id.appwidget_content, e.message)
                }

                override fun onDone() {
                    views.setViewVisibility(R.id.appwidget_progressbar, View.INVISIBLE)
                    AppWidgetManager.getInstance(context).updateAppWidget(widgetId, views)
                    callback()
                }

            }).execute(datasourceInfo)
        }

        internal fun getAutoUpdatePendingIntent(context: Context, widgetId: Int): PendingIntent {
            val intent = Intent(context, RemoteData::class.java)
            intent.setAction(ACTION_APPWIDGET_UPDATE)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, arrayOf(widgetId).toIntArray())
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)))

            return PendingIntent.getBroadcast(context, widgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        internal fun scheduleUpdateIn(context: Context, pendingIntent: PendingIntent, time_in_minutes: Int) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.MINUTE, time_in_minutes)

            alarmManager?.cancel(pendingIntent)

            if (time_in_minutes > 0) {
                alarmManager?.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            }
        }

        internal fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.remote_data)

            val pendingIntent = getAutoUpdatePendingIntent(context, appWidgetId)
            views.setOnClickPendingIntent(R.id.appwidget_root, pendingIntent)

            val configurationIntent = Intent(context.applicationContext, RemoteDataConfigureActivity::class.java)
            configurationIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            val pendingConfigurationIntent = PendingIntent.getActivity(context, appWidgetId, configurationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setOnClickPendingIntent(R.id.appwidget_titlebar, pendingConfigurationIntent)

            val wakeLock: PowerManager.WakeLock =
                (context.getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                    newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "RemoteData::WidgetUpdate").apply {
                        acquire()
                    }
                }

            loadWidgetConfig(context, appWidgetId) { widgetConfig, datasourceInfo ->
                if (widgetConfig === null || datasourceInfo === null) {
                    views.setTextViewText(R.id.appwidget_label, "Error")
                    views.setTextViewText(R.id.appwidget_content, "(misconfigured)")
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                } else {
                    views.setTextViewText(R.id.appwidget_label, widgetConfig.label)
                    scheduleUpdateIn(context, pendingIntent, widgetConfig.update_interval_in_minutes)

//                    val info = DatasourceInfo(
//                        null,
//                        "Test",
//                        "https://papi.ertrzyiks.me/food-time?access_token=123&query=%7BlastEntryDate%28spaceId%3A%20%22f54dc7be-e6ba-4fbe-ac39-935fae3f23c2%22%29%7D"
//                        "https://papi.ertrzyiks.me/food-time?access_token=123&query=%7Bnow%2ClastEntryDate%28spaceId%3A%20%22f54dc7be-e6ba-4fbe-ac39-935fae3f23c2%22%29%7D"
//                    )
                    loadAndUpdateData(context, views, appWidgetId, datasourceInfo, widgetConfig.path) {
                        wakeLock.release()
                    }
                }
            }

        }
    }
}

