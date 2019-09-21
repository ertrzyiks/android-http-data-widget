package com.example.httpdatawidget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.httpdatawidget.storage.AppDatabase
import com.example.httpdatawidget.storage.DatasourceInfo
import com.example.httpdatawidget.storage.WidgetConfig
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.widget.*


/**
 * The configuration screen for the [RemoteData] AppWidget.
 */
class RemoteDataConfigureActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    internal var mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    internal var mAppWidgetLabel: EditText? = null
    internal var mAppWidgetPath: EditText? = null
    internal var mAppWidgetInterval: TextView? = null
    internal var mSeekBar: SeekBar? = null
    internal var db: AppDatabase? = null
    internal var mWidgetConfig: WidgetConfig? = null

    internal var mOnClickListener: View.OnClickListener = View.OnClickListener {
        val context = this@RemoteDataConfigureActivity

        mWidgetConfig?.label = mAppWidgetLabel?.text.toString()
        mWidgetConfig?.path = mAppWidgetPath?.text.toString()

        Thread {
            if (mWidgetConfig?.id === null) {
                db!!.widgetConfigDao().insert(mWidgetConfig!!)
            } else {
                db!!.widgetConfigDao().update(mWidgetConfig!!)
            }

            // It is the responsibility of the configuration activity to update the app widget
            val appWidgetManager = AppWidgetManager.getInstance(context)
            RemoteData.updateAppWidget(context, appWidgetManager, mAppWidgetId)

            // Make sure we pass back the original appWidgetId
            val resultValue = Intent()
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
            setResult(Activity.RESULT_OK, resultValue)
            finish()
        }.start()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        mWidgetConfig?.datasource_info_id = null
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        val item = parent?.getItemAtPosition(pos) as DatasourceInfo
        mWidgetConfig?.datasource_info_id = item?.id
    }

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(Activity.RESULT_CANCELED)

        setContentView(R.layout.remote_data_configure)
        mAppWidgetLabel = findViewById<EditText>(R.id.appwidget_label)
        mAppWidgetPath = findViewById<EditText>(R.id.appwidget_path)
        mAppWidgetInterval = findViewById(R.id.appwidget_configure_interval)
        mSeekBar = findViewById(R.id.appwidget_configure_seekbar)
        findViewById<View>(R.id.add_button).setOnClickListener(mOnClickListener)

        val appWidgetDatasourceHint = findViewById<TextView>(R.id.appwidget_configure_datasource_hint)
        val indexOfCreate = appWidgetDatasourceHint.text.indexOf("create new one")
        val spanable = SpannableString(appWidgetDatasourceHint.text)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
            }
        }

        spanable.setSpan(clickableSpan, indexOfCreate, indexOfCreate + "create new one".length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spanable.setSpan(ForegroundColorSpan(Color.parseColor("#0000FF")), indexOfCreate, indexOfCreate + "create new one".length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        appWidgetDatasourceHint.movementMethod = LinkMovementMethod.getInstance()
        appWidgetDatasourceHint.text = spanable

        mSeekBar?.max = AutoUpdateInterval.totalOptions() - 1
        mSeekBar?.setOnSeekBarChangeListener(onSeekBarChange)

        // Find the widget id from the intent.
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        db = AppDatabase.getInstance(applicationContext)

        val spinner = findViewById<Spinner>(R.id.spinner)
        val adapter = DatasourceSpinnerAdapter(applicationContext, R.layout.spinner_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = this

        Thread {
            mWidgetConfig = db!!.widgetConfigDao().findByWidgetId(mAppWidgetId)


            if (mWidgetConfig == null) {
                mWidgetConfig = WidgetConfig(mAppWidgetId)
            }

            var selectedDatasourceInfoId = mWidgetConfig?.datasource_info_id


            runOnUiThread {
                mAppWidgetLabel?.setText(mWidgetConfig?.label ?: "Title")
                mAppWidgetPath?.setText(mWidgetConfig?.path ?: "")
                mSeekBar?.progress = AutoUpdateInterval.indexForInterval(mWidgetConfig?.update_interval_in_minutes ?: 0)

                db!!.datasourceInfoDao().getAll().observe(this, Observer {
                    adapter.clear()
                    val items = arrayOf(DatasourceInfo(null, "None", "")).union(it)
                    adapter.addAll(items)

                    if (selectedDatasourceInfoId !== null) {
                        spinner.setSelection(items.indexOfFirst { info -> info.id === selectedDatasourceInfoId })
                    }
                })
            }
        }.start()
    }

    internal val onSeekBarChange = object: SeekBar.OnSeekBarChangeListener {
        override fun onStartTrackingTouch(p0: SeekBar?) {}
        override fun onStopTrackingTouch(p0: SeekBar?) {}

        override fun onProgressChanged(bar: SeekBar?, progress: Int, fromUser: Boolean) {
            mAppWidgetInterval?.text = AutoUpdateInterval.labelForIndex(progress)
            mWidgetConfig?.update_interval_in_minutes = AutoUpdateInterval.valueForIndex(progress)
        }
    }

    companion object {

        private val PREFS_NAME = "com.example.httpdatawidget.RemoteData"
        private val PREF_PREFIX_KEY = "appwidget_"

        // Write the prefix to the SharedPreferences object for this widget
        internal fun saveTitlePref(context: Context, appWidgetId: Int, text: String) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
            prefs.putString(PREF_PREFIX_KEY + appWidgetId, text)
            prefs.apply()
        }

        // Read the prefix from the SharedPreferences object for this widget.
        // If there is no preference saved, get the default from a resource
        internal fun loadTitlePref(context: Context, appWidgetId: Int): String {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            val titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null)
            return titleValue ?: context.getString(R.string.appwidget_label)
        }

        internal fun deleteTitlePref(context: Context, appWidgetId: Int) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
            prefs.remove(PREF_PREFIX_KEY + appWidgetId)
            prefs.apply()
        }
    }
}

