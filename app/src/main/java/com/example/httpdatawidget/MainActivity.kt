package com.example.httpdatawidget

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.lifecycle.Observer
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.lang.Exception
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(){
    private var db: DatasourceInfoBase? = null
    private lateinit var dbWorkerThread: DbWorkerThread
    private val uiHandler = Handler()
    private var listView: ListView by Delegates.notNull()

    internal val onButtonClick = View.OnClickListener {
        println("Click")

        Thread(Runnable {
            var item = DatasourceInfo()
            item.name = "New data source"
            db!!.datasourceInfoDao().insert(item)
        }).start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbWorkerThread = DbWorkerThread("dbWorkerThread")
        dbWorkerThread.start()

        db = DatasourceInfoBase.getInstance(this)

        listView = findViewById(R.id.list)

        val adapter = DatasourceListAdapter(this, R.layout.list_item)
        listView.adapter = adapter

        db!!.datasourceInfoDao().getAll().observe(this, Observer {
            adapter.clear()
            adapter.addAll(it)
        })
//
        var el: FloatingActionButton = findViewById(R.id.add_button)
        el.setOnClickListener(onButtonClick)
    }

    override fun onDestroy() {
        DatasourceInfoBase.destroyInstance()
        dbWorkerThread.quit()
        super.onDestroy()
    }

    private fun fetchDatasourceInfos() {
        dbWorkerThread.postTask(Runnable {
            val info = db?.datasourceInfoDao()?.getAll()

            uiHandler.post {

            }
        })
    }
}
