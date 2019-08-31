package com.example.httpdatawidget

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import java.lang.Exception

class MainActivity : AppCompatActivity(){
    internal val onButtonClick = View.OnClickListener {
      println("Click")

        LoadData(applicationContext, object: LoadDataCallback<String> {
            override fun onSuccess(value: String) {
                var el: TextView = findViewById(R.id.text)
                el.text = value
            }

            override fun onFailure(e: Exception) {

            }
        }).execute()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//
//        var el: TextView = findViewById(R.id.text)
//        el.setOnClickListener(onButtonClick)
    }
}
