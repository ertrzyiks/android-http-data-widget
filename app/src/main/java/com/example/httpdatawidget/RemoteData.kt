package com.example.httpdatawidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
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

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {

        internal fun loadAndUpdateData(context: Context, views: RemoteViews, widgetId: Int) {
            views.setViewVisibility(R.id.appwidget_progressbar, View.VISIBLE)
            AppWidgetManager.getInstance(context).updateAppWidget(widgetId, views)

            LoadData(context, object: LoadDataCallback<String> {
                override fun onSuccess(value: String) {
                    views.setViewVisibility(R.id.appwidget_progressbar, View.GONE)
                    views.setTextViewText(R.id.appwidget_text, value)
                    AppWidgetManager.getInstance(context).updateAppWidget(widgetId, views)
                }

                override fun onFailure(e: Exception) {

                }
            }).execute()
        }

        internal fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.remote_data)

            val widgetText = RemoteDataConfigureActivity.loadTitlePref(context, appWidgetId)
            views.setTextViewText(R.id.appwidget_label, widgetText)

            val intent = Intent(context, RemoteData::class.java)
            intent.setAction(ACTION_APPWIDGET_UPDATE)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, arrayOf(appWidgetId).toIntArray())

            val pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.appwidget_root, pendingIntent)

            loadAndUpdateData(context, views, appWidgetId)
        }
    }
}

