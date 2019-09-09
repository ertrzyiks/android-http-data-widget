package com.example.httpdatawidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.RemoteViews
import com.example.httpdatawidget.storage.DatasourceInfo
import java.lang.Exception

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
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
//            Toast.makeText(context, "Widget has been updated! ", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            RemoteDataConfigureActivity.deleteTitlePref(context, appWidgetId)
        }
    }

    companion object {

        internal fun loadAndUpdateData(context: Context, views: RemoteViews, widgetId: Int) {
            views.setViewVisibility(R.id.appwidget_progressbar, View.VISIBLE)
            AppWidgetManager.getInstance(context).updateAppWidget(widgetId, views)

            val info = DatasourceInfo(
                null,
                "Test",
                "https://papi.ertrzyiks.me/food-time?access_token=123&query=%7BlastEntryDate%28spaceId%3A%20%22f54dc7be-e6ba-4fbe-ac39-935fae3f23c2%22%29%7D",
                true
            )

            LoadData(context, object: LoadDataCallback<Response>{
                override fun onSuccess(value: Response) {
                    var text = JsonSource.get(value.contentBody)
//                    views.setTextViewText(R.id.appwidget_text, text)
                }

                override fun onFailure(e: Exception) {
                    //                    views.setTextViewText(R.id.appwidget_text, e.message)
                }

                override fun onDone() {
                    views.setViewVisibility(R.id.appwidget_progressbar, View.INVISIBLE)
                    AppWidgetManager.getInstance(context).updateAppWidget(widgetId, views)
                }

            }).execute(info)
        }

        internal fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.remote_data)

            val widgetText = RemoteDataConfigureActivity.loadTitlePref(context, appWidgetId)
//            views.setTextViewText(R.id.appwidget_label, widgetText)

            val intent = Intent(context, RemoteData::class.java)
            intent.setAction(ACTION_APPWIDGET_UPDATE)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, arrayOf(appWidgetId).toIntArray())
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)))

            val pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.appwidget_root, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)

            loadAndUpdateData(context, views, appWidgetId)
        }
    }
}

